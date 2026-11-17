// Bless be the name of the LORD GOD
package com.den.steward.backend.states.allTabState

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.LineChartDataCollection
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class AllTabDataState (
    val transactions: ImmutableMap<String, List<Transaction>> = persistentMapOf(),
    val lineChartDataCollection: LineChartDataCollection = LineChartDataCollection(),
    val allTransactionSummary: AllTransactionSummary = AllTransactionSummary(),
)