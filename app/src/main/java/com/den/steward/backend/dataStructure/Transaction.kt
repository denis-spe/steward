package com.den.steward.backend.dataStructure

import androidx.compose.runtime.Stable
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.title

@Stable
sealed class Transaction {
    abstract val id: String
    abstract val type: TransactionType

    abstract val createdAt: Long
    @Stable
    data class Earnings(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.EARNINGS,
        override val createdAt: Long = System.currentTimeMillis(),
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectAmount: Boolean = false,
    ) : Transaction() {

    }

    @Stable
    data class Expense(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.EXPENSE,
        override val createdAt: Long = System.currentTimeMillis(),
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectAmount: Boolean = false,
    ) : Transaction()

    @Stable
    data class Savings(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.SAVINGS,
        override val createdAt: Long = System.currentTimeMillis(),
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectAmount: Boolean = false,
    ) : Transaction()

    @Stable
    data class Lent(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.LENT,
        val repayment: List<Repayment> = emptyList(),
        override val createdAt: Long = System.currentTimeMillis(),
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectAmount: Boolean = false
    ) : Transaction() {
        val totalRepayment: Double get() = repayment.sumOf { it.amount }
        val remainingAmount: Double get() = amount - totalRepayment
    }

    @Stable
    data class Debt(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.DEBT,
        val refund: List<Refund> = emptyList(),
        override val createdAt: Long = System.currentTimeMillis(),
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectAmount: Boolean = false
    ) : Transaction() {
        val totalRefund: Double get() = refund.sumOf { it.amount }
        val remainingAmount: Double get() = amount - totalRefund
    }


    @Stable
    data class Repayment(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.REPAYMENT,
        override val createdAt: Long = System.currentTimeMillis(),
        val lent: Lent = Lent(),
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectAmount: Boolean = false
    ) : Transaction()

    @Stable
    data class Refund(
        override val id: String = "",
        val label: String = "",
        val note: String = "",
        val amount: Double = 0.0,
        override val type: TransactionType = TransactionType.REFUND,
        override val createdAt: Long = System.currentTimeMillis(),
        val debt: Debt = Debt(),
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val affectAmount: Boolean = false
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
        val goalType: GoalType = GoalType.AMOUNT,
        override val createdAt: Long = System.currentTimeMillis(),
        val startedAt: Long = System.currentTimeMillis(),
        val endAt: Long = System.currentTimeMillis(),
        val status: GoalStatus = GoalStatus.NOT_STARTED,
        val repeatable: RecurrencePattern = RecurrencePattern.NONE,
    ) : Transaction() {
        val totalAttain = attain.sumOf { it.value }
        val remainingValue: Double get() = value - totalAttain

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
            if (now > this.endAt && this.totalAttain > this.value ) return this.copy(status = GoalStatus.COMPLETED)
            if (now > this.endAt) return this.copy(status = GoalStatus.FIELD)

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

    val getLabel: String
        get() {
            return when (this) {
                is Earnings -> this.label.title
                is Expense -> this.label.title
                is Lent -> this.label.title
                is Debt -> this.label.title
                is Goal -> this.label.title
                is Repayment -> this.label.title
                is Refund -> this.label.title
                is Attain -> "${this.goal.label.title} Attainment"
                is Achievement -> "${this.goal.label.title} Achievement"
                is Savings -> this.label.title
            }
        }

    val getPaymentMethodOrNull: PaymentMethod?
        get() {
            return when (this) {
                is Earnings -> this.paymentMethod
                is Expense -> this.paymentMethod
                is Lent -> this.paymentMethod
                is Debt -> this.paymentMethod
                is Goal -> null
                is Repayment -> this.paymentMethod
                is Refund -> this.paymentMethod
                is Savings -> this.paymentMethod
                is Attain -> null
                is Achievement -> null
            }
        }

    val getNote: String
        get() {
            return when (this) {
                is Earnings -> this.note
                is Expense -> this.note
                is Lent -> this.note
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
                is Earnings -> this.amount
                is Expense -> this.amount
                is Lent -> this.amount
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
                is Earnings -> this.amount.formatToAmount()
                is Expense -> this.amount.formatToAmount()
                is Lent -> this.amount.formatToAmount()
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

    val getAffectAmount: String?
        get() {
            val affectAmount = when (this) {
                is Earnings -> this.affectAmount
                is Expense -> this.affectAmount
                is Lent -> this.affectAmount
                is Debt -> this.affectAmount
                is Savings -> this.affectAmount
                is Goal -> null
                is Repayment -> this.affectAmount
                is Refund -> this.affectAmount
                is Attain -> null
                is Achievement -> null
            }

            if (affectAmount == null) return null
            return if (affectAmount) "Yes" else "No"
        }
}