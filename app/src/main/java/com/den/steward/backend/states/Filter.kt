package com.den.steward.backend.states

import com.den.steward.R
import com.den.steward.backend.entitles.TransactionType

enum class Filter {
    ALL,
    EARNINGS,
    EXPENSE,
    GOAL,
    SAVINGS,
    REPAYMENT,
    SETTLEMENT,
    ATTAIN,
    LENT,
    DEBT,
    PLAN,
    UNPAID,
    PAID,
    AFFECTED,
    UN_AFFECTED;


    companion object {
        val Filter.toTransactionType: TransactionType? get() {
            return when (this) {
                EARNINGS -> TransactionType.EARNINGS
                EXPENSE -> TransactionType.EXPENSE
                GOAL -> TransactionType.GOAL
                SAVINGS -> TransactionType.SAVINGS
                REPAYMENT -> TransactionType.REPAYMENT
                SETTLEMENT -> TransactionType.SETTLEMENT
                ATTAIN -> TransactionType.ATTAIN
                LENT -> TransactionType.LENT
                DEBT -> TransactionType.DEBT
                PLAN -> TransactionType.PLAN
                else -> null
            }
        }

        val Filter.icon: Int get() {
            return when (this) {
                ALL -> R.drawable.filter
                EARNINGS -> R.drawable.ic_earnings
                EXPENSE -> R.drawable.ic_expense
                GOAL -> R.drawable.ic_finance_target
                SAVINGS -> R.drawable.ic_savings
                REPAYMENT -> R.drawable.ic_repayment
                SETTLEMENT -> R.drawable.ic_refund
                ATTAIN -> R.drawable.ic_attain
                LENT -> R.drawable.ic_loan
                DEBT -> R.drawable.ic_debt
                PLAN -> R.drawable.ic_plan
                UNPAID -> R.drawable.ic_unpaid
                PAID -> R.drawable.ic_paid
                AFFECTED -> R.drawable.ic_affected
                UN_AFFECTED -> R.drawable.ic_un_affected
            }
        }
    }
}