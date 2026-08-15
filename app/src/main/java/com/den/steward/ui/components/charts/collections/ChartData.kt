package com.den.steward.ui.components.charts.collections

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class ChartData(
    val x: List<Double> = listOf(),
    val y: List<Double> = listOf(),
    val color: Color = Color.Gray,
    // Optional label shown in legends. Keep default null to avoid breaking existing callers.
    val label: String? = null
) {
    /**
     * Returns true if both x and y are not empty
     */
    fun isXYNotEmpty(): Boolean {
        return x.isNotEmpty() && y.isNotEmpty()
    }
}