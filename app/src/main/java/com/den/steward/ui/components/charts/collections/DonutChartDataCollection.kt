// Bless be the Name of the LORD
package com.den.steward.ui.components.charts.collections

import com.den.steward.ui.components.charts.DonutChartData

data class DonutChartDataCollection(
    var items: List<DonutChartData> = emptyList()
) {
    internal var totalAmount: Float = items.sumOf { it.amount.toDouble() }.toFloat()
        private set
}
