// Bless be the name of LORD GOD of hosts
package com.den.steward.backend.states

import androidx.compose.runtime.Immutable

@Immutable
data class YesterdayTransactionSummary(
    val flow: Double = 0.0,
    val transactionSize: Int = 0,
    val highTransactionActivityAmount: Double = 0.0,
    val highTransactionActivityLabel: String = "",
    val lowTransactionActivityAmount: Double = 0.0,
    val lowTransactionActivityLabel: String = ""
)
