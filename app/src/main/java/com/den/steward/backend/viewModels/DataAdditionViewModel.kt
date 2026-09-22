// Grace and truth came through JESUS CHRIST
package com.den.steward.backend.viewModels

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.AddDataUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.helper.toEpochMillis
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DataAdditionViewModel @Inject constructor(
    private val addDataUseCase: AddDataUseCase,
    private val dataFetchUseCase: DataFetchUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "DataAdditionViewModel"
    }

    // ==================== States ====================
    private val _dataAdditionState = MutableStateFlow(DataAdditionState())
    val dataAdditionState = _dataAdditionState.asStateFlow()

    // ============= State updates ===========
    /**
     * Updates the show transaction addition bottom sheet state
     * @param show The new value for showTransactionAdditionBottomSheet
     */
    fun updateShowTransactionAdditionBottomSheet(show: Boolean) {
        _dataAdditionState.update { it.copy(showTransactionAdditionBottomSheet = show) }
    }

    /**
     * Updates the show transaction type bottom sheet state
     * @param show The new value for showTransactionTypeBottomSheet
     */
    fun updateShowTransactionTypeBottomSheet(show: Boolean) {
        _dataAdditionState.update { it.copy(showTransactionTypeBottomSheet = show) }
    }

    /**
     * Updates the correct label field state
     * @param state The new value for transaction field state for label
     */
    fun updateIsLabelCorrect(state: TransactionFieldState) {
        _dataAdditionState.update { it.copy(isLabelCorrect = state) }
    }

    /**
     * Updates the transaction amount field state
     * @param state The new value for isAmountCorrect
     */
    fun updateIsAmountCorrect(state: TransactionFieldState) {
        _dataAdditionState.update { it.copy(isAmountCorrect = state) }
    }

    /**
     * Updates the transaction payment method
     * @param paymentMethod The new value for paymentMethod
     */
    fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        _dataAdditionState.update { it.copy(paymentMethod = paymentMethod) }
    }

    /**
     * Updates the transaction affect amount
     * @param isAffectingAmount The new value for isAffectingAmount
     */
    fun updateIsAffectingAmount(isAffectingAmount: Boolean) {
        _dataAdditionState.update { it.copy(isAffectingAmount = isAffectingAmount) }
    }

    fun updateCorrectLabel(label: String) {
        _dataAdditionState.update { it.copy(
            currentLabel = label,
            isLabelCorrect = if (label.isNotEmpty()) TransactionFieldState.Success else TransactionFieldState.Initial
        ) }
    }

    fun updateCorrectNote(note: String) {
        _dataAdditionState.update { it.copy(currentNote = note) }
    }

    fun updateCorrectAmount(amount: String) {
        val amountValue = amount.toDoubleOrNull()
        val isValid = amount.isNotEmpty() && amountValue != null && amountValue > 0.0
        _dataAdditionState.update { it.copy(
            currentAmount = amount,
            isAmountCorrect = if (isValid) TransactionFieldState.Success else TransactionFieldState.Initial
        ) }
    }

    fun updateLocalDateCreatedAt(localDateCreatedAt: LocalDate) {
        _dataAdditionState.update { it.copy(localDateCreatedAt = localDateCreatedAt) }
    }

    fun updateLocalTimeCreatedAt(localTimeCreatedAt: LocalTime) {
        _dataAdditionState.update { it.copy(localTimeCreatedAt = localTimeCreatedAt) }
    }

    fun updateStartAt(startAt: LocalDateTime) {
        _dataAdditionState.update { it.copy(
            startAt = startAt,
            isStartNotEqualToEndDateTime = TransactionFieldState.Initial
        ) }
    }

    fun updateEndAt(endAt: LocalDateTime) {
        _dataAdditionState.update { it.copy(
            endAt = endAt,
            isStartNotEqualToEndDateTime = TransactionFieldState.Initial
        ) }
    }

    fun updateRecurrence(recurrence: RecurrencePattern) {
        _dataAdditionState.update { it.copy(recurrence = recurrence) }
    }


    fun updateIsStartNotEqualToEndDateTime(isStartNotEqualToEndDateTime: TransactionFieldState) {
        _dataAdditionState.update { it.copy(isStartNotEqualToEndDateTime = isStartNotEqualToEndDateTime) }
    }

    fun updateMainBottomSheetState(show: Boolean) {
        _dataAdditionState.update { it.copy(showMainBottomSheet = show) }
    }

    fun onBottomDrawerSheetItemClick(transactionType: TransactionType) {
        _dataAdditionState.update { it.copy(
            showTransactionAdditionBottomSheet = true,
            showTransactionTypeBottomSheet = false,
            selectedTransactionType = transactionType,
        ) }
    }

    fun updateSelectedFulfillmentTransactionType(transactionType: TransactionType?) {
        _dataAdditionState.update { it.copy(selectedFulfillmentTransactionType = transactionType) }
    }

    fun updateSelectedParentTransaction(transaction: Transaction?) {
        _dataAdditionState.update { it.copy(selectedParentTransaction = transaction) }
    }

    fun updateShowFulfillmentTransactionAdditionBottomSheet(show: Boolean) {
        _dataAdditionState.update { it.copy(showFulfillmentTransactionAdditionBottomSheet = show) }
    }

    fun fetchTransactionByType(transactionType: TransactionType): StateFlow<DataState<List<Transaction>>> {
        return dataFetchUseCase.fetchAllTransactions
            .map { state ->
                if (state is DataState.Success) {
                    val transactions = state.data.filter { it.type == transactionType }
                    DataState.Success(transactions)
                } else {
                    state
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
        _dataAdditionState.update {
            DataAdditionState()
        }
    }

    fun updateShowFulfillmentTransactionTypeBottomSheet(expanded: Boolean) {
        _dataAdditionState.update { it.copy(showFulfillmentTransactionTypeBottomSheet = expanded) }
    }

    fun updateIsFulfillmentValid(transactionFieldState: TransactionFieldState) {
        _dataAdditionState.update {
            it.copy(
                isFulfillmentValid = transactionFieldState
            )
        }
    }

    // ============= Data Addition ===========
    fun addCoreEntriesTransaction() {
        val currentState = _dataAdditionState.value
        
        // Guard against multiple clicks
        if (currentState.isSaving) return

        val isLabelEmpty = currentState.currentLabel.isEmpty()
        val amountValue = currentState.currentAmount.toDoubleOrNull()
        val isAmountInvalid = currentState.currentAmount.isEmpty() ||
                amountValue == null ||
                (amountValue == 0.0)
        val isGoalInvalid = currentState.selectedTransactionType == TransactionType.GOAL &&
                currentState.endAt.toEpochMillis() <= currentState.startAt.toEpochMillis()
        val isPlanType = currentState.selectedTransactionType == TransactionType.PLAN

        if (isLabelEmpty || (!isPlanType && isAmountInvalid) || isGoalInvalid) {
            _dataAdditionState.update { it.copy(
                isLabelCorrect = if (isLabelEmpty) TransactionFieldState.Error("Label cannot be empty") else TransactionFieldState.Success,
                isAmountCorrect = when {
                    isPlanType -> TransactionFieldState.Success
                    isAmountInvalid -> TransactionFieldState.Error("Amount cannot be empty or 0")
                    else -> TransactionFieldState.Success
                },
                isStartNotEqualToEndDateTime = if (isGoalInvalid) TransactionFieldState.Error("End time must be after start time") else TransactionFieldState.Success
            ) }
            return
        }

        currentState.selectedTransactionType?.let { transactionType ->
            // 1. Immediately update state to indicate saving and close the sheet
            // This provides instant feedback to the user and prevents double clicks
            _dataAdditionState.update { it.copy(
                isSaving = true,
                showTransactionAdditionBottomSheet = false,
                showMainBottomSheet = false
            ) }

            val createdAt = currentState.localDateCreatedAt.atTime(currentState.localTimeCreatedAt).toEpochMillis()

            viewModelScope.launch {
                try {
                    // 2. Perform the database operation
                    addDataUseCase.addTransaction(
                        DataTransferToViewModel(
                            label = currentState.currentLabel,
                            amount = currentState.currentAmount,
                            note = currentState.currentNote,
                            createdAt = createdAt,
                            paymentMethod = currentState.paymentMethod,
                            isAffectingAmount = currentState.isAffectingAmount,
                            transactionType = transactionType,
                            startedAt = currentState.startAt.toEpochMillis(),
                            endAt = currentState.endAt.toEpochMillis(),
                            repeatable = currentState.recurrence
                        )
                    )
                    // 3. Reset the state after adding the transaction
                    reset()
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding transaction of type $transactionType", e)
                    // 4. In case of error, stop the loading state and maybe keep the sheet closed or notify user
                    _dataAdditionState.update { it.copy(isSaving = false) }
                }
            }
        }
    }

    fun addFulfillmentTransaction() {
        _dataAdditionState.update { it.copy(isFulfillmentValid = TransactionFieldState.Initial) }

        val currentState = _dataAdditionState.value
        val labelText = currentState.label.text.toString()
        val amountText = currentState.amount.text.toString()
        val noteText = currentState.note.text.toString()

        if (currentState.isSaving || currentState.selectedParentTransaction == null) {
            _dataAdditionState.update {
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

        if (isAmountInvalid) {
            _dataAdditionState.update { it.copy(
                isAmountCorrect = TransactionFieldState.Error("Amount cannot be empty or 0")
            ) }
            return
        }

        val parent = currentState.selectedParentTransaction
        val createdAt = currentState.localDateCreatedAt.atTime(currentState.localTimeCreatedAt).toEpochMillis()

        val fulfillment = try {
            when (currentState.selectedFulfillmentTransactionType) {
                TransactionType.REPAYMENT -> {
                    if (parent !is Transaction.Lent) throw IllegalArgumentException("Parent must be Lent")
                    Transaction.Repayment(
                        amount = amountValue,
                        createdAt = createdAt,
                        lent = parent,
                        paymentMethod = currentState.paymentMethod,
                        affectAmount = currentState.isAffectingAmount,
                        label = "${parent.label} repayment",
                        note = noteText
                    )
                }

                TransactionType.SETTLEMENT -> {
                    if (parent !is Transaction.Debt) throw IllegalArgumentException("Parent must be Debt")
                    Transaction.Settlement(
                        amount = amountValue,
                        createdAt = createdAt,
                        debt = parent,
                        paymentMethod = currentState.paymentMethod,
                        affectAmount = currentState.isAffectingAmount,
                        label = "${parent.label} settlement",
                        note = noteText
                    )
                }

                TransactionType.ATTAIN -> {
                    if (parent !is Transaction.Goal) throw IllegalArgumentException("Parent must be Goal")
                    if (amountValue <= 0.0) throw IllegalArgumentException("Amount must be greater than 0")
                    if (parent.status == GoalStatus.COMPLETED) throw IllegalArgumentException("Goal is already completed")
                    if (parent.status == GoalStatus.FAILED) throw IllegalArgumentException("Goal is already failed")

                    Transaction.Attain(
                        value = amountValue,
                        createdAt = createdAt,
                        goal = parent
                    )
                }
                else -> throw IllegalArgumentException("Invalid fulfillment type")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error building fulfillment transaction", e)
            _dataAdditionState.update {
                it.copy(
                    isSaving = false,
                    isFulfillmentValid = TransactionFieldState.Error(e.message ?: "Invalid fulfillment")
                )
            }
            return
        }

        // Immediately update state to indicate saving and close the sheet
        _dataAdditionState.update { it.copy(
            isSaving = true,
            showFulfillmentTransactionAdditionBottomSheet = false,
            showMainBottomSheet = false
        ) }

        viewModelScope.launch {
            try {
                // Perform the database operation
                addDataUseCase.addFulfillment(
                    parent.id,
                    fulfillment
                )
                // Reset the state after adding
                reset()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding fulfillment transaction", e)
                _dataAdditionState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun addPlanFulfillment() {
        _dataAdditionState.update { it.copy(isFulfillmentValid = TransactionFieldState.Initial) }

        val currentState = _dataAdditionState.value
        val labelText = currentState.label.text.toString()
        val amountText = currentState.amount.text.toString()
        val noteText = currentState.note.text.toString()

        if (currentState.isSaving || currentState.selectedParentTransaction == null) {
            _dataAdditionState.update {
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

        if (isAmountInvalid) {
            _dataAdditionState.update { it.copy(
                isAmountCorrect = TransactionFieldState.Error("Amount cannot be empty or 0")
            ) }
            return
        }

        val parent = currentState.selectedParentTransaction
        val createdAt = currentState.localDateCreatedAt.atTime(currentState.localTimeCreatedAt).toEpochMillis()

        val fulfillment = try {
            when (currentState.selectedFulfillmentTransactionType) {
                TransactionType.PLAN_FULFILLMENT -> {
                    if (parent !is Transaction.Plan) throw IllegalArgumentException("Parent must be Plan")
                    Transaction.PlanFulfillment(
                        value = amountValue,
                        createdAt = createdAt,
                        plan = parent,
                        label = labelText,
                        note = noteText,
                        status = currentState.planStatus,
                        fulfillmentType = currentState.selectedFulfillmentTransactionType
                    )
                }

                else -> throw IllegalArgumentException("Invalid fulfillment type")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error building fulfillment transaction", e)
            _dataAdditionState.update {
                it.copy(
                    isSaving = false,
                    isFulfillmentValid = TransactionFieldState.Error(e.message ?: "Invalid fulfillment")
                )
            }
            return
        }

        // Immediately update state to indicate saving and close the sheet
        _dataAdditionState.update { it.copy(
            isSaving = true,
        ) }

        viewModelScope.launch {
            try {
                // Perform the database operation
                addDataUseCase.addFulfillment(
                    parent.id,
                    fulfillment
                )

                _dataAdditionState.update {
                    it.copy(
                        isSaving = true,
                        amount = TextFieldState(),
                        label = TextFieldState(),
                        note = TextFieldState(),
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error adding fulfillment transaction", e)
                _dataAdditionState.update { it.copy(isSaving = false) }
            }
        }
    }
}