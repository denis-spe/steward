package com.den.steward.ui.components.charts.collections

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ChartDataCollection(
    val chartData: ImmutableList<ChartData> = persistentListOf()
) {

    /**
     * Check if all chart data is not empty
     */
    fun allAreNotEmpty(): Boolean {
        return chartData.isNotEmpty() && chartData.all { it.isXYNotEmpty() }
    }
}