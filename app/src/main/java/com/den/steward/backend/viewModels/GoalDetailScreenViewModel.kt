package com.den.steward.backend.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataNotifier
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.goalDetailScreenState.GoalData
import com.den.steward.backend.states.goalDetailScreenState.GoalDetailUiState
import com.den.steward.backend.useCase.AddDataUseCase
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.UpdateTransactionUseCase
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.title
import com.den.steward.helper.toEpochMillis
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.fields.TransactionFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


@HiltViewModel
class GoalDetailScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    private val addDataUseCase: AddDataUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    private val dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "GoalDetailScreenViewModel"
    }

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

    fun reset() {
        val newState = GoalDetailUiState()

        // Clear the edit buffer of each TextFieldState
        newState.amount.clearText()

        _goalDetailUiState.value = newState
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

    fun updateCorrectAmount(amount: String) {
        val amountValue = amount.toDoubleOrNull()
        val isValid = amount.isNotEmpty() && amountValue != null && amountValue > 0.0
        _goalDetailUiState.update {
            it.copy(
                currentAmount = amount,
                isAmountCorrect = if (isValid) TransactionFieldState.Success else TransactionFieldState.Initial
            )
        }
    }

    fun updateLocalDateCreatedAt(localDateCreatedAt: LocalDate) {
        _goalDetailUiState.update { it.copy(localDateCreatedAt = localDateCreatedAt) }
    }

    fun updateLocalTimeCreatedAt(localTimeCreatedAt: LocalTime) {
        _goalDetailUiState.update { it.copy(localTimeCreatedAt = localTimeCreatedAt) }
    }

    fun updateShowFulfillmentTransactionAdditionBottomSheet(show: Boolean) {
        _goalDetailUiState.update { it.copy(showFulfillmentTransactionAdditionBottomSheet = show) }
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

    fun addAttainTransaction(parent: Transaction) {
        _goalDetailUiState.update { it.copy(isFulfillmentValid = TransactionFieldState.Initial) }

        val currentState = _goalDetailUiState.value
        val amountText = currentState.amount.text.toString()

        if (currentState.isSaving) {
            _goalDetailUiState.update {
                it.copy(
                    isFulfillBtnClick = true,
                    isSaving = false,
                    isFulfillmentValid = TransactionFieldState.Error("Select transaction to fulfill")
                )
            }
            return
        }

        val amountValue = amountText.toDoubleOrNull()
        val isAmountInvalid = amountText.isEmpty() || amountValue == null || amountValue == 0.0
        val createdAt =
            currentState.localDateCreatedAt.atTime(currentState.localTimeCreatedAt).toEpochMillis()

        if (isAmountInvalid) {
            _goalDetailUiState.update {
                it.copy(
                    isAmountCorrect = TransactionFieldState.Error("Amount cannot be empty or 0")
                )
            }
            return
        }

        val fulfillment = try {
            if (parent !is Transaction.Goal) throw IllegalArgumentException("Parent must be Goal")
            if (amountValue <= 0.0) throw IllegalArgumentException("Amount must be greater than 0")
            if (parent.status == GoalStatus.COMPLETED) throw IllegalArgumentException("Goal is already completed")
            if (parent.status == GoalStatus.FAILED) throw IllegalArgumentException("Goal is already failed")

            Transaction.Attain(
                value = amountValue,
                createdAt = createdAt,
                goal = parent
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error building fulfillment transaction", e)
            _goalDetailUiState.update {
                it.copy(
                    isSaving = false,
                    isFulfillmentValid = TransactionFieldState.Error(
                        e.message ?: "Invalid fulfillment"
                    )
                )
            }
            return
        }

        // Immediately update state to indicate saving and close the sheet with instant notification
        val notifier = DataNotifier(
            text = "${TransactionType.ATTAIN.name.title} added successfully",
            icon = parent.getSelectedIcon.icon,
            amount = amountValue.formatToAmount()
        )

        reset()
        _goalDetailUiState.update { it.copy(dataNotifier = notifier) }

        viewModelScope.launch {
            try {
                // Perform the database operation
                addDataUseCase.addFulfillment(
                    parent.id,
                    fulfillment
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error adding fulfillment transaction", e)
                _goalDetailUiState.update { it.copy(isSaving = false) }
            }
        }

        // Automatically clear notification state after 4 seconds
        viewModelScope.launch {
            delay(4000.milliseconds)
            _goalDetailUiState.update { it.copy(dataNotifier = null) }
        }
    }

    fun updateIsAmountCorrect(transactionFieldState: TransactionFieldState) {

    }
}
