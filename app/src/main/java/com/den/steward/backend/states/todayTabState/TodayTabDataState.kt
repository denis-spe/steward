package com.den.steward.backend.states.todayTabState

import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.ui.components.charts.DonutChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TodayTabDataState(
    val transactions: ImmutableList<Transaction> = persistentListOf(),
    val donutChartData: ImmutableList<DonutChartData> = persistentListOf(),
    val balanceStatStates: BalanceStatStates = BalanceStatStates(),
    val liabilitiesPaymentStatsState: LiabilitiesPaymentStatsState = LiabilitiesPaymentStatsState(),
    val donutSummaryStat: String = "",
)