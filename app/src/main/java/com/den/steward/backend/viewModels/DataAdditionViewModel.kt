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
     * Updates the transaction label
     * @param state The new value for label
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

    /**
     * Updates the transaction type
     * @param transactionType The transaction type to update to
     */
    fun updateTransactionType(transactionType: TransactionType) {
        _dataAdditionState.update { it.copy(selectedTransactionType = transactionType) }
    }

    fun updateCorrectLabel(label: String) {
        _dataAdditionState.update { it.copy(currentLabel = label) }
    }

    fun updateCorrectNote(note: String) {
        _dataAdditionState.update { it.copy(currentNote = note) }
    }

    fun updateCorrectAmount(amount: String) {
        _dataAdditionState.update { it.copy(currentAmount = amount) }
    }

    fun updateLocalDateCreatedAt(localDateCreatedAt: LocalDate) {
        _dataAdditionState.update { it.copy(localDateCreatedAt = localDateCreatedAt) }
    }

    fun updateLocalTimeCreatedAt(localTimeCreatedAt: LocalTime) {
        _dataAdditionState.update { it.copy(localTimeCreatedAt = localTimeCreatedAt) }
    }

    fun updateStartAt(startAt: LocalDateTime) {
        _dataAdditionState.update { it.copy(startAt = startAt) }
    }

    fun updateEndAt(endAt: LocalDateTime) {
        _dataAdditionState.update { it.copy(endAt = endAt) }
    }

    fun updateRecurrence(recurrence: RecurrencePattern) {
        _dataAdditionState.update { it.copy(recurrence = recurrence) }
    }


    fun updateIsStartNotEqualToEndDateTime(isStartNotEqualToEndDateTime: TransactionFieldState) {
        _dataAdditionState.update { it.copy(isStartNotEqualToEndDateTime = isStartNotEqualToEndDateTime) }
    }

    fun onTransactionBottomDrawerSheetDismiss() {
        reset()
    }

    fun onBottomDrawerSheetItemClick(transactionType: TransactionType) {
        _dataAdditionState.update { it.copy(
            showTransactionAdditionBottomSheet = true,
            showTransactionTypeBottomSheet = false,
            selectedTransactionType = transactionType
        ) }
    }

    fun onBottomDrawerSheetDismiss() {
        reset()
    }

    fun addCoreEntriesTransaction() {

        dataAdditionState.value.selectedTransactionType?.let { transactionType ->
            viewModelScope.launch {

                // Add the transaction to the database
                addDataUseCase.addTransaction(
                    DataTransferToViewModel(
                        label = dataAdditionState.value.currentLabel,
                        amount = dataAdditionState.value.currentAmount,
                        note = dataAdditionState.value.currentNote,
                        createdAt = dataAdditionState.value.localDateTimeCreatedAt.toEpochMillis(),
                        paymentMethod = dataAdditionState.value.paymentMethod,
                        isAffectingAmount = dataAdditionState.value.isAffectingAmount,
                        transactionType = transactionType,
                        startedAt = dataAdditionState.value.startAt.toEpochMillis(),
                        endAt = dataAdditionState.value.endAt.toEpochMillis(),
                        repeatable = dataAdditionState.value.recurrence
                    )
                )

                // Reset the state after adding the transaction
                reset()
            }
        }
    }

    fun reset() {
        _dataAdditionState.update { DataAdditionState() }
    }
}