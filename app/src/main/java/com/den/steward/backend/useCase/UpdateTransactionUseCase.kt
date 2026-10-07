package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataUpdateState
import com.den.steward.helper.combine
import com.den.steward.ui.components.SelectedIcon
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
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

    fun onLocalTimeChangeUpdate(time: LocalTime) {
        _dataUpdateState.update {
            it.copy(
                localTimeCreatedAt = time
            )
        }
    }

    fun onLocalDateChangeUpdate(date: LocalDate) {
        _dataUpdateState.update {
            it.copy(
                localDateCreatedAt = date
            )
        }
    }

    suspend fun updateTransaction(): Boolean {
        val transaction = dataUpdateState.value.selectedTransaction ?: return false
        val adjustmentEntries: List<TransactionType> = listOf(
            TransactionType.REPAYMENT,
            TransactionType.SETTLEMENT,
            TransactionType.ATTAIN,
            TransactionType.ACHIEVEMENT
        )


        val createdAt = dataUpdateState.value.localDateCreatedAt
            .combine(dataUpdateState.value.localTimeCreatedAt)


        if (transaction.type in adjustmentEntries) {
            val parentTransaction = transaction.getParentTransaction ?: return false

            val amountOrValue = dataUpdateState.value
                .amount.text.toString()
                .toDoubleOrNull() ?: 0.0
            val parentAmount = parentTransaction.getAmountOrValue ?: 0.0

            if (amountOrValue > parentAmount) {
                _dataUpdateState.update {
                    it.copy(
                        isAmountCorrect = TransactionFieldState.Error(
                            "Amount cannot be greater than parent transaction"
                        )
                    )
                }
                updateShowBottomSheet(true)
                return false
            } else {
                updateShowBottomSheet(false)
            }


            val newTransaction = when(transaction.type) {
                TransactionType.REPAYMENT -> {
                    val repayment = transaction as Transaction.Repayment
                    repayment.copy(
                        amount = amountOrValue,
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount,
                        createdAt = createdAt
                    )
                }
                TransactionType.SETTLEMENT -> {
                    val settlement = transaction as Transaction.Settlement
                    settlement.copy(
                        amount = amountOrValue,
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount,
                        createdAt = createdAt
                    )
                }
                TransactionType.ATTAIN -> {
                    val attain = transaction as Transaction.Attain
                    attain.copy(
                        value = amountOrValue
                    )
                }

                TransactionType.ACHIEVEMENT -> {
                    val achievement = transaction as Transaction.Achievement
                    achievement.copy(
                        value = amountOrValue
                    )
                }
                else -> return false
            }

            updateDateUseCase.updateTransactionFulfillment(
                parentTransaction.id,
                transaction.id,
                newTransaction
            )
            return true
        } else {
            val newTransaction = when(transaction.type) {
                TransactionType.EARNINGS -> {
                    val earnings = transaction as Transaction.Earnings
                    earnings.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount,
                        selectedIcon = dataUpdateState.value.selectedIcon.icon,
                        createdAt = createdAt
                    )
                }
                TransactionType.EXPENSE -> {
                    val expense = transaction as Transaction.Expense
                    expense.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount,
                        selectedIcon = dataUpdateState.value.selectedIcon.icon,
                        createdAt = createdAt
                    )
                }

                TransactionType.LENT -> {
                    val lent = transaction as Transaction.Lent
                    lent.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount,
                        selectedIcon = dataUpdateState.value.selectedIcon.icon,
                        createdAt = createdAt
                    )
                }

                TransactionType.DEBT -> {
                    val debt = transaction as Transaction.Debt
                    debt.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount,
                        selectedIcon = dataUpdateState.value.selectedIcon.icon,
                        createdAt = createdAt
                    )
                }

                TransactionType.SAVINGS -> {
                    val savings = transaction as Transaction.Savings
                    savings.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        amount = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        paymentMethod = dataUpdateState.value.paymentMethod,
                        affectAmount = dataUpdateState.value.isAffectingAmount,
                        selectedIcon = dataUpdateState.value.selectedIcon.icon,
                        createdAt = createdAt
                    )
                }

                TransactionType.GOAL -> {
                    val goal = transaction as Transaction.Goal
                    goal.copy(
                        label = dataUpdateState.value.label.text.toString(),
                        note = dataUpdateState.value.note.text.toString(),
                        value = dataUpdateState.value.amount.text.toString().toDoubleOrNull() ?: 0.0,
                        selectedIcon = dataUpdateState.value.selectedIcon.icon,
                        createdAt = createdAt
                    )
                }
                else -> return false
            }

            updateShowBottomSheet(false)
            _dataUpdateState.update {
                it.copy(
                    isSaving = false
                )
            }

            updateDateUseCase.updateTransaction(
                transactionId = transaction.id,
                newTransaction = newTransaction
            )
            return true
        }
    }
}