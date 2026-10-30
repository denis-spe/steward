// Glory be to the LORD of host
package com.den.steward.backend.states.liabilityScreenState

import androidx.compose.runtime.Stable

@Stable
data class LiabilityMainSummary(
    val liabilityFlow: Double = 0.0,
    val paidLiability: Double = 0.0,
    val unpaidLiability: Double = 0.0,
    val paidLiabilityPercent: Double = 0.0,
    val unpaidLiabilityPercent: Double = 0.0
)
