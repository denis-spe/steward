package com.den.steward.backend.states.yesterdayTabState

import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class YesterdayTabState(
    val summary: YesterdayTransactionSummary = YesterdayTransactionSummary(),
    val transactions: ImmutableList<Transaction> = persistentListOf(),
    val chartDataCollection: ChartDataCollection = ChartDataCollection()
)
