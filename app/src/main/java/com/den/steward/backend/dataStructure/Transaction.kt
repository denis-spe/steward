package com.den.steward.backend.dataStructure

import androidx.compose.runtime.Stable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

sealed class Transaction {
    abstract val id: String
    abstract val type: TransactionType

    abstract val createdAt: Long
    @Stable
    data class Earning(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.EARNINGS,
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Expense(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.EXPENSE,
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Loan(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.LOAN,
        val repayment: List<Repayment> = emptyList(),
        val totalRepayment: Double = repayment.sumOf { it.amount },
        val remainingAmount: Double = amount - totalRepayment,
       override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Debt(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.DEBT,
        val refund: List<Refund> = emptyList(),
        val totalRefund: Double = refund.sumOf { it.amount },
        val remainingAmount: Double = amount - totalRefund,
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()


    @Stable
    data class Repayment(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.REPAYMENT,
        override val createdAt: Long = System.currentTimeMillis(),
        val loan: Loan = Loan()
    ) : Transaction()

    @Stable
    data class Refund(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.REFUND,
        override val createdAt: Long = System.currentTimeMillis(),
        val debt: Debt = Debt()
    ) : Transaction()

    @Stable
    data class Goal(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val value: Double = 0.0,
        override val type: TransactionType = TransactionType.GOAL,
        val attain: List<Attain> = emptyList(),
        val achievement: List<Achievement> = emptyList(),
        val remainingValue: Double = value - attain.sumOf { it.value },
        val totalValue: Double = attain.sumOf { it.value },
        val goalType: GoalType = GoalType.AMOUNT,
        override val createdAt: Long = System.currentTimeMillis()
    ) : Transaction()

    @Stable
    data class Attain(
        override val id: String = "",
        val value: Double = 0.0,
        override val type: TransactionType = TransactionType.ATTAIN,
        override val createdAt: Long = System.currentTimeMillis(),
        val goal: Goal = Goal()
    ) : Transaction()

    @Stable
    data class Achievement(
        override val id: String = "",
        val value: Double = 0.0,
        override val type: TransactionType = TransactionType.ACHIEVEMENT,
        override val createdAt: Long = System.currentTimeMillis(),
        val goal: Goal = Goal()
    ) : Transaction()


    val toMap: MutableMap<String, Any>
        get() {
            val mapping = mutableMapOf<String, Any>(
                "id" to this.id,
                "type" to this.type.name,
                // Use the object's createdAt if it's not "now" (useful for testing/backdating)
                // Otherwise let Firestore set the precise server time.
                "createdAt" to if (this.createdAt < System.currentTimeMillis() - 1000) {
                    Timestamp(java.util.Date(this.createdAt))
                } else {
                    FieldValue.serverTimestamp()
                }
            )

            when(this) {
                is Earning -> {
                    mapping["amount"] = this.amount
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                }

                is Expense -> {
                    mapping["amount"] = this.amount
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                }

                is Loan -> {
                    mapping["amount"] = this.amount
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                }

                is Debt -> {
                    mapping["amount"] = this.amount
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                }

                is Goal -> {
                    mapping["value"] = this.value
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                    mapping["goalType"] = this.goalType.name
                }

                is Attain -> {
                    mapping["value"] = this.value
                }

                is Repayment -> {
                    mapping["amount"] = this.amount
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                }

                is Refund -> {
                    mapping["amount"] = this.amount
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                }

                is Achievement -> {
                    mapping["value"] = this.value
                }
            }
            return mapping
        }
    val getLabelOrNull: String?
        get() {
            return when (this) {
                is Earning -> this.label
                is Expense -> this.label
                is Loan -> this.label
                is Debt -> this.label
                is Goal -> this.label
                is Repayment -> this.label
                is Refund -> this.label
                is Attain -> null
                is Achievement -> null
            }
        }
    val getNoteOrNull: String?
        get() {
            return when (this) {
                is Earning -> this.note
                is Expense -> this.note
                is Loan -> this.note
                is Debt -> this.note
                is Goal -> this.note
                is Repayment -> this.note
                is Refund -> this.note
                is Attain -> null
                is Achievement -> null
            }
        }
    val getAmountOrNull: Double?
        get() {
            return when (this) {
                is Earning -> this.amount
                is Expense -> this.amount
                is Loan -> this.amount
                is Debt -> this.amount
                is Goal -> null
                is Repayment -> this.amount
                is Refund -> this.amount
                is Attain -> null
                is Achievement -> null
            }
        }
    val getValueOrNull: Double?
        get() {
            return when (this) {
                is Goal -> this.value
                is Attain -> this.value
                is Achievement -> this.value
                else -> null
            }
        }
}