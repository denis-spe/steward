// Bless be the name of the LORD GOD
package com.den.steward.backend.states.allTabState

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDate

@Immutable
data class AllTabDataState (
    val transactions: ImmutableMap<String, List<Transaction>> = persistentMapOf(),
    val chartDataCollection: ChartDataCollection = ChartDataCollection(),
    val allTransactionSummary: AllTransactionSummary = AllTransactionSummary(),
)