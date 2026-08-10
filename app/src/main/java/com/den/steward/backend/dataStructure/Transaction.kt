package com.den.steward.backend.dataStructure

import androidx.compose.runtime.Stable
import com.den.steward.helper.formatResult
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.title
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
    data class Savings(
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
        override val createdAt: Long = System.currentTimeMillis(),
        val startedAt: Long = System.currentTimeMillis(),
        val endAt: Long = System.currentTimeMillis(),
        val status: GoalStatus = GoalStatus.NOT_STARTED,
        val repeatable: GoalRepeat = GoalRepeat.NONE
    ) : Transaction() {

        fun calculateSchedule(now: Long): Goal {
            val schedule = this.repeatable.onSchedule

            if (schedule == 0L) return this

            return this.copy(
                startedAt = now,
                endAt = now + schedule
            )
        }

        fun calculateStatus(now: Long): Goal {
            if (this.status != GoalStatus.NOT_STARTED) return this
            if (now < this.startedAt) return this.copy(status = GoalStatus.NOT_STARTED)
            if (now > this.endAt) return this.copy(status = GoalStatus.COMPLETED)
            return this.copy(status = GoalStatus.IN_PROGRESS)
        }
    }

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
        val startAt: Long = System.currentTimeMillis(),
        val endAt: Long = System.currentTimeMillis(),
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

                is Savings -> {
                    mapping["amount"] = this.amount
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                }

                is Goal -> {
                    mapping["value"] = this.value
                    mapping["label"] = this.label
                    mapping["note"] = this.note
                    mapping["goalType"] = this.goalType.name
                    mapping["status"] = this.status.name
                    mapping["repeatable"] = this.repeatable.name
                    mapping["startedAt"] = if (this.startedAt < System.currentTimeMillis() - 1000) {
                        Timestamp(java.util.Date(this.startedAt))
                    } else {
                        FieldValue.serverTimestamp()
                    }
                    mapping["endAt"] = if (this.endAt < System.currentTimeMillis() - 1000) {
                        Timestamp(java.util.Date(this.endAt))
                    } else {
                        FieldValue.serverTimestamp()
                    }
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
    val getLabel: String
        get() {
            return when (this) {
                is Earning -> this.label.title
                is Expense -> this.label.title
                is Loan -> this.label.title
                is Debt -> this.label.title
                is Goal -> this.label.title
                is Repayment -> this.label.title
                is Refund -> this.label.title
                is Attain -> "${this.goal.label.title} Attainment"
                is Achievement -> "${this.goal.label.title} Achievement"
                is Savings -> this.label.title
            }
        }
    val getNote: String
        get() {
            return when (this) {
                is Earning -> this.note
                is Expense -> this.note
                is Loan -> this.note
                is Debt -> this.note
                is Goal -> this.note
                is Repayment -> this.note
                is Refund -> this.note
                is Savings -> this.note
                is Attain -> "Attained ${this.value} of ${this.goal.value}"
                is Achievement -> "Achieved ${this.value} of ${this.goal.value}"
            }
        }
    val getAmountOrValue: Double?
        get() {
            return when (this) {
                is Earning -> this.amount
                is Expense -> this.amount
                is Loan -> this.amount
                is Savings -> this.amount
                is Debt -> this.amount
                is Goal -> this.value
                is Repayment -> this.amount
                is Refund -> this.amount
                is Attain -> this.value
                is Achievement -> this.value
            }
        }

    val getFormattedAmountOrValue: String
        get() {
            return when (this) {
                is Earning -> this.amount.formatToAmount()
                is Expense -> this.amount.formatToAmount()
                is Loan -> this.amount.formatToAmount()
                is Debt -> this.amount.formatToAmount()
                is Savings -> this.amount.formatToAmount()
                is Goal -> {
                    if (this.goalType == GoalType.AMOUNT) this.value.formatToAmount()
                    else this.value.toString()
                }
                is Repayment -> this.amount.formatToAmount()
                is Refund -> this.amount.formatToAmount()
                is Attain -> {
                    if (this.goal.goalType == GoalType.AMOUNT) this.value.formatToAmount()
                    else this.value.toString()
                }
                is Achievement -> {
                    if (this.goal.goalType == GoalType.AMOUNT) this.value.formatToAmount()
                    else this.value.toString()
                }
            }
        }
}