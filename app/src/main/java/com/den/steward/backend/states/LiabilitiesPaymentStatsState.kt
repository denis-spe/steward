package com.den.steward.backend.states

import androidx.compose.runtime.Immutable

@Immutable
data class LiabilitiesPaymentStatsState(
    val totalLoan: Double = 0.0,
    val totalDebt: Double = 0.0,
    val unPaidLoan: Double = 0.0,
    val unPaidDebt: Double = 0.0,
    val paidCount: Double = 0.0,
    val unPaidCount: Double = 0.0
)
