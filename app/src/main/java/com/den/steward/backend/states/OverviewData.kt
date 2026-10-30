// Glo
package com.den.steward.backend.states

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.overviewTabState.OverviewSummary

data class OverviewData(
    val summary: OverviewSummary,
    val groupedTransactions: Map<String, List<Transaction>>
)