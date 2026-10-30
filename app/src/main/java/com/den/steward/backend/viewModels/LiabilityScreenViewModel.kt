package com.den.steward.backend.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.liabilityScreenState.LiabilityDataState
import com.den.steward.backend.states.liabilityScreenState.LiabilityMainSummary
import com.den.steward.backend.states.liabilityScreenState.LiabilityScreenUiState
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.UpdateTransactionUseCase
import com.den.steward.helper.calculateFlow
import com.den.steward.helper.formattedDate
import com.den.steward.helper.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LiabilityScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "TransactionScreenVM"
    }

    private val _liabilityScreenUiState = MutableStateFlow(LiabilityScreenUiState())
    val liabilityScreenUiState = _liabilityScreenUiState.asStateFlow()

    val dataUpdateState = updateTransaction.dataUpdateState


    val liabilityDataState = dataFetchUseCase.fetchAllTransactions.map { state ->
        when (state) {
            is DataState.Success -> {
                val transactions = state.data

                val liabilities = listOf(
                    TransactionType.DEBT,
                    TransactionType.LENT
                )

                val liabilityFlow = transactions.filter { transaction ->
                    transaction.type in listOf(
                        TransactionType.SETTLEMENT,
                        TransactionType.REPAYMENT,
                        TransactionType.DEBT,
                        TransactionType.LENT
                    )
                }.calculateFlow

                val now = LocalDate.now()

                val liabilityTransactions = transactions
                    .filter {
                        it.type in liabilities
                    }

                val paidLiability = liabilityTransactions.map {
                    when (it) {
                        is Transaction.Debt -> it.settlement.sumOf { settlement -> settlement.amount }
                        is Transaction.Lent -> it.repayment.sumOf { repayment -> repayment.amount }
                        else -> 0.0
                    }
                }.sumOf { it }

                val unpaidLiability = liabilityTransactions.map {
                    when (it) {
                        is Transaction.Debt -> it.remainingAmount
                        is Transaction.Lent -> it.remainingAmount
                        else -> 0.0
                    }
                }.sumOf { it }

                val totalSum = paidLiability + unpaidLiability

                val paidLiabilityPercent =
                    if (totalSum == 0.0) 0.0 else (paidLiability / totalSum) * 100
                val unpaidLiabilityPercent =
                    if (totalSum == 0.0) 0.0 else (unpaidLiability / totalSum) * 100


                val liabilityMainSummary = LiabilityMainSummary(
                    liabilityFlow = liabilityFlow,
                    paidLiability = paidLiability,
                    unpaidLiability = unpaidLiability,
                    paidLiabilityPercent = paidLiabilityPercent,
                    unpaidLiabilityPercent = unpaidLiabilityPercent
                )

                val groupedLiabilityTransaction = liabilityTransactions
                    .groupBy {
                        val createdAt = it.createdAt.toLocalDateTime()
                        when {
                            now == createdAt.toLocalDate() -> "Today"
                            now.minusDays(1) == createdAt.toLocalDate() -> "Yesterday"
                            else -> createdAt.formattedDate
                        }
                    }.toImmutableMap()

                val liabilityDataState = LiabilityDataState(
                    liabilityTransactions = groupedLiabilityTransaction,
                    liabilityMainSummary = liabilityMainSummary
                )

                DataState.Success(liabilityDataState)
            }

            is DataState.Error -> DataState.Error(state.message)
            is DataState.Loading -> DataState.Loading
        }
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    // ===================== Delete =====================
    fun deleteTransaction(transactionToBeDeleted: SnapshotStateList<Transaction>? = null) {
        viewModelScope.launch {
            val transaction =
                _liabilityScreenUiState.value.selectedTransactionToDelete ?: return@launch
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
        _liabilityScreenUiState.update {
            it.copy(
                selectedTransactionToDelete = transaction,
                openDeleteDialog = transaction != null
            )
        }
    }

    fun updateOpenDeleteDialog(show: Boolean) {
        _liabilityScreenUiState.update {
            it.copy(openDeleteDialog = show)
        }
    }

    fun updateOpenDeleteAllDialog(show: Boolean) {
        _liabilityScreenUiState.update {
            it.copy(openDeleteAllDialog = show)
        }
    }

    fun deleteAllTransactions(transactionToBeDeleted: SnapshotStateList<Transaction>) {
        val itemsToDelete = transactionToBeDeleted.toList()
        updateOpenDeleteAllDialog(false)

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                // Ideally, update your deletionUseCase to handle a list:
                // deletionUseCase.deleteTransactions(itemsToDelete)
                // Or loop synchronously on IO if batching isn't available yet:
                itemsToDelete.forEach { transaction ->
                    deletionUseCase.deleteTransaction(transaction)
                }
            }.onSuccess {
                // Clear all at once on the Main thread when batch operation finishes
                launch(Dispatchers.Main) {
                    transactionToBeDeleted.clear()
                }
            }.onFailure { e ->
                Log.e(TAG, "Failed to delete transactions", e)
            }
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