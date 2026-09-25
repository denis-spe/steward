// Glory be to name of LORD GOD
package com.den.steward.helper

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import kotlin.collections.forEach

fun calculateFlow(transactions: List<Transaction>): Double {
    var incoming = 0.0
    var outgoing = 0.0

    transactions.forEach { transaction ->
        if (transaction.getAffectAmount == "Yes") {
            val amount = transaction.getAmountOrValue ?: 0.0
            when (transaction.type) {
                TransactionType.EARNINGS,
                TransactionType.DEBT,
                TransactionType.REPAYMENT -> incoming += amount

                TransactionType.EXPENSE,
                TransactionType.LENT,
                TransactionType.SAVINGS,
                TransactionType.SETTLEMENT -> outgoing += amount
                else -> {}
            }
        }
    }
    return incoming - outgoing
}