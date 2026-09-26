package com.den.steward.backend.states

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
    PLAN;

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
                ALL -> null
            }
        }
    }
}