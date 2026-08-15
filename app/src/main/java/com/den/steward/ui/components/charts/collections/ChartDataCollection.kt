package com.den.steward.ui.components.charts.collections

data class ChartDataCollection(
    val chartData: List<ChartData> = emptyList()
) {

    /**
     * Check if all chart data is not empty
     */
    fun allAreNotEmpty(): Boolean {
        return chartData.isNotEmpty() && chartData.all { it.isXYNotEmpty() }
    }
}