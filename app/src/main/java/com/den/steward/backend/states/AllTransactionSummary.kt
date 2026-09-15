package com.den.steward.backend.states

data class AllTransactionSummary(
    val flow: Double,
    val transactionSize: Int,
    val totalReceived: Double,
    val totalSpent: Double,
    val totalSavings: Double
)
