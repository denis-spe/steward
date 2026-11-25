package com.den.steward.backend.viewModels

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.goalDetailScreenState.GoalData
import com.den.steward.backend.states.goalDetailScreenState.GoalDetailUiState
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.UpdateTransactionUseCase
import com.den.steward.helper.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


@HiltViewModel
class GoalDetailScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    private val dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    private val _goalDetailUiState = MutableStateFlow(GoalDetailUiState())
    val goalDetailUiState = _goalDetailUiState.asStateFlow()
    private val transactionFlows = ConcurrentHashMap<String, StateFlow<DataState<Transaction>>>()
    val dataUpdateState = updateTransaction.dataUpdateState

    fun getTransaction(transactionId: String): StateFlow<DataState<Transaction>> {
        return transactionFlows.getOrPut(transactionId) {
            dataFetchUseCase.fetchAllTransactions
                .map { dataState ->
                    when (dataState) {
                        is DataState.Success -> {
                            val found = dataState.data.firstOrNull { it.id == transactionId }
                            if (found != null) {
                                DataState.Success(found)
                            } else {
                                val fetched =
                                    dataFetchUseCase.getTransaction(transactionId).getOrNull()
                                if (fetched != null) {
                                    DataState.Success(fetched)
                                } else {
                                    DataState.Error("Transaction not found")
                                }
                            }
                        }

                        is DataState.Error -> DataState.Error(dataState.message)
                        is DataState.Loading -> DataState.Loading
                    }
                }
                .distinctUntilChanged()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = DataState.Loading
                )
        }
    }

    fun fetchFulfillmentTransactions(transaction: Transaction): StateFlow<DataState<GoalData>> {
        return combine(
            dataFetchUseCase.fetchAllFulfillment(
                transaction,
                fulfillmentType = TransactionType.ATTAIN
            ),
            dataFetchUseCase.fetchAllFulfillment(
                transaction,
                fulfillmentType = TransactionType.ACHIEVEMENT
            )
        ) { (attain, achieve) ->
            when {
                attain is DataState.Success && achieve is DataState.Success -> {
                    DataState.Success(
                        GoalData(
                            attain = attain.data.sortedByDescending { it.createdAt.toLocalDateTime() },
                            achieve = achieve.data.sortedByDescending { it.createdAt.toLocalDateTime() }
                        )
                    )
                }

                attain is DataState.Error -> DataState.Error(attain.message)
                achieve is DataState.Error -> DataState.Error(achieve.message)
                else -> DataState.Loading
            }
        }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DataState.Loading
            )
    }

    // ===================== Delete =====================
    fun deleteTransaction(transactionToBeDeleted: SnapshotStateList<Transaction>? = null) {
        viewModelScope.launch {
            val transaction =
                _goalDetailUiState.value.selectedTransactionToDelete ?: return@launch
            runCatching {
                deletionUseCase.deleteTransaction(transaction)
            }.onSuccess {
                transactionToBeDeleted?.removeIf { it.id == transaction.id }
            }
        }

        // Reset the state
        updateSelectedTransactionToDelete(null)
        updateOpenDeleteDialog(false)
    }

    fun updateSelectedTransactionToDelete(transaction: Transaction?) {
        _goalDetailUiState.update {
            it.copy(
                selectedTransactionToDelete = transaction,
                openDeleteDialog = transaction != null
            )
        }
    }

    fun updateOpenDeleteDialog(show: Boolean) {
        _goalDetailUiState.update {
            it.copy(openDeleteDialog = show)
        }
    }

    fun updateOpenDeleteAllDialog(show: Boolean) {
        _goalDetailUiState.update {
            it.copy(openDeleteAllDialog = show)
        }
    }

    fun deleteAllTransactions(transactionToBeDeleted: SnapshotStateList<Transaction>) {
        val itemsToDelete = transactionToBeDeleted.toList().map { it.id }
        updateOpenDeleteAllDialog(false)
        transactionToBeDeleted.clear()

        viewModelScope.launch(Dispatchers.IO) {
            deletionUseCase.deletingMultipleTransaction(
                transactionIds = itemsToDelete
            )
        }
    }


    // ===================== Update Transaction =========================
    fun onUpdateTransaction() {
        viewModelScope.launch {
            if (updateTransaction.updateTransaction()) {
                updateTransaction.onReset()
            }
        }
    }
}
