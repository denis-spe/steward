// Glory be to LORD GOD of hosts
package com.den.steward.backend.states.overviewTabState

import androidx.compose.runtime.Stable

@Stable
data class OverviewSummary(
    val netFlow: String,
    val liabilities: String,
    val goalAchieved: String,
    val goalFailed: String
)
