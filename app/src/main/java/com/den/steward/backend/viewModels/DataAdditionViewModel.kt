// Grace and truth came through JESUS CHRIST
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.useCase.AddDataUseCase
import com.den.steward.helper.toEpochMillis
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DataAdditionViewModel @Inject constructor(
    private val addDataUseCase: AddDataUseCase,
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

    fun onBottomDrawerSheetItemClick(transactionType: TransactionType) {
        _dataAdditionState.update { it.copy(
            showTransactionAdditionBottomSheet = true,
            showTransactionTypeBottomSheet = false,
            selectedTransactionType = transactionType
        ) }
    }

    fun addCoreEntriesTransaction() {
        val currentState = _dataAdditionState.value
        
        // Guard against multiple clicks
        if (currentState.isSaving) return

        val isLabelEmpty = currentState.currentLabel.isEmpty()
        val amountValue = currentState.currentAmount.toDoubleOrNull()
        val isAmountInvalid = currentState.currentAmount.isEmpty() || amountValue == null || amountValue == 0.0
        val isGoalInvalid = currentState.selectedTransactionType == TransactionType.GOAL &&
                currentState.endAt.toEpochMillis() <= currentState.startAt.toEpochMillis()

        if (isLabelEmpty || isAmountInvalid || isGoalInvalid) {
            _dataAdditionState.update { it.copy(
                isLabelCorrect = if (isLabelEmpty) TransactionFieldState.Error("Label cannot be empty") else TransactionFieldState.Success,
                isAmountCorrect = if (isAmountInvalid) TransactionFieldState.Error("Amount cannot be empty or 0") else TransactionFieldState.Success,
                isStartNotEqualToEndDateTime = if (isGoalInvalid) TransactionFieldState.Error("End time must be after start time") else TransactionFieldState.Success
            ) }
            return
        }

        currentState.selectedTransactionType?.let { transactionType ->
            // 1. Immediately update state to indicate saving and close the sheet
            // This provides instant feedback to the user and prevents double clicks
            _dataAdditionState.update { it.copy(
                isSaving = true,
                showTransactionAdditionBottomSheet = false
            ) }

            viewModelScope.launch {
                try {
                    // 2. Perform the database operation
                    addDataUseCase.addTransaction(
                        DataTransferToViewModel(
                            label = currentState.currentLabel,
                            amount = currentState.currentAmount,
                            note = currentState.currentNote,
                            createdAt = currentState.localDateTimeCreatedAt.toEpochMillis(),
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
                    // 4. In case of error, stop the loading state and maybe keep the sheet closed or notify user
                    _dataAdditionState.update { it.copy(isSaving = false) }
                    // Log the error or show a notification
                }
            }
        }
    }

    fun reset() {
        _dataAdditionState.update { 
            DataAdditionState(
                showTransactionAdditionBottomSheet = false,
                showTransactionTypeBottomSheet = false
            )
        }
    }
}