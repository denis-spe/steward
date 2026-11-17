package com.den.steward.backend.states.yesterdayTabState

import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class YesterdayTabState(
    val summary: YesterdayTransactionSummary = YesterdayTransactionSummary(),
    val transactions: ImmutableList<Transaction> = persistentListOf(),
    val chartDataCollection: ImmutableList<Bars> = persistentListOf()
)
