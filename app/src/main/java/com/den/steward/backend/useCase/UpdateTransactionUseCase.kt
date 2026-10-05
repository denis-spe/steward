package com.den.steward.backend.useCase

import androidx.compose.foundation.text.input.TextFieldState
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataUpdateState
import com.den.steward.ui.components.SelectedIcon
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val updateDateUseCase: UpdateDateUseCase
) {

    private val _dataUpdateState = MutableStateFlow(DataUpdateState())
    val dataUpdateState = _dataUpdateState.asStateFlow()

    fun updateSelectedTransaction(transaction: Transaction?) {
        _dataUpdateState.update {
            if (transaction == null) {
                DataUpdateState()
            } else {
                DataUpdateState(
                    selectedTransaction = transaction,
                    showBottomSheet = true
                )
            }
        }
    }

    fun updateShowBottomSheet(show: Boolean) {
        _dataUpdateState.update {
            it.copy(
                showBottomSheet = show
            )
        }
    }

    fun updateCurrentLabel(currentLabel: String) {
        _dataUpdateState.update {
            it.copy(
                currentLabel = currentLabel
            )
        }
    }

    fun updateIsLabelCorrect(isLabelCorrect: TransactionFieldState) {
        _dataUpdateState.update {
            it.copy(
                isLabelCorrect = isLabelCorrect
            )
        }
    }

    fun updateIsAmountCorrect(isAmountCorrect: TransactionFieldState) {
        _dataUpdateState.update {
            it.copy(
                isAmountCorrect = isAmountCorrect
            )
        }
    }

    fun updateCurrentNote(currentNote: String) {
        _dataUpdateState.update {
            it.copy(
                currentNote = currentNote
            )
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        _dataUpdateState.update {
            it.copy(
                paymentMethod = paymentMethod
            )
        }
    }

    fun updateIsAffectingAmount(isAffectingAmount: Boolean) {
        _dataUpdateState.update {
            it.copy(
                isAffectingAmount = isAffectingAmount
            )
        }
    }

    fun updateSelectedIcon(selectedIcon: SelectedIcon) {
        _dataUpdateState.update {
            it.copy(
                selectedIcon = selectedIcon
            )
        }
    }

    fun updateCurrentAmount(currentAmount: String) {
        _dataUpdateState.update {
            it.copy(
                currentAmount = currentAmount
            )
        }
    }

    fun onReset() {
        _dataUpdateState.update {
            DataUpdateState()
        }
    }

    suspend fun updateTransaction() {
        val transaction = dataUpdateState.value.selectedTransaction ?: return
        val adjustmentEntries: List<TransactionType> = listOf(
            TransactionType.REPAYMENT,
            TransactionType.SETTLEMENT,
            TransactionType.ATTAIN,
        )

        updateShowBottomSheet(false)

        if (transaction.type in adjustmentEntries) {
            val parentTransaction = transaction.getParentTransaction ?: return

            val newTransaction = when(transaction.type) {
                TransactionType.REPAYMENT -> {
                    val repayment = transaction as Transaction.Repayment
                    repayment.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount
                    )
                }
                TransactionType.SETTLEMENT -> {
                    val settlement = transaction as Transaction.Settlement
                    settlement.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount
                    )
                }
                TransactionType.ATTAIN -> {
                    val attain = transaction as Transaction.Attain
                    attain.copy(
                        value = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                    )
                }
                else -> return
            }

            updateDateUseCase.updateTransactionFulfillment(
                parentTransaction.id,
                transaction.id,
                newTransaction
            )
        } else {
            val newTransaction = when(transaction.type) {
                TransactionType.EARNINGS -> {
                    val earnings = transaction as Transaction.Earnings
                    earnings.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount
                    )
                }
                TransactionType.EXPENSE -> {
                    val expense = transaction as Transaction.Expense
                    expense.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount
                    )
                }

                TransactionType.LENT -> {
                    val lent = transaction as Transaction.Lent
                    lent.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount
                    )
                }

                TransactionType.DEBT -> {
                    val debt = transaction as Transaction.Debt
                    debt.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount
                    )
                }

                TransactionType.SAVINGS -> {
                    val savings = transaction as Transaction.Savings
                    savings.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount
                    )
                }

                TransactionType.GOAL -> {
                    val goal = transaction as Transaction.Goal
                    goal.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        value = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                    )
                }
                else -> return
            }

            updateDateUseCase.updateTransaction(
                transactionId = transaction.id,
                newTransaction = newTransaction
            )
        }
    }
}