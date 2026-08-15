package com.den.steward.ui.components.charts.collections

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Simple data holder for candlestick series.
 * x: list of x positions (usually timestamps or indices)
 * open/high/low/close: lists of values, must be same size as x
 */
@Immutable
data class CandlestickDataCollection(
    val x: List<Int> = listOf(),
    val opening: List<Int> = listOf(),
    val high: List<Int> = listOf(),
    val low: List<Int> = listOf(),
    val closing: List<Int> = listOf(),
    val color: Color = Color.Gray
) {
    fun allNotEmpty(): Boolean {
        return x.isNotEmpty() &&
                opening.isNotEmpty() &&
                high.isNotEmpty() &&
                low.isNotEmpty() &&
                closing.isNotEmpty()
    }

    fun allHasSameSize(): Boolean {
        return x.size == opening.size &&
                x.size == high.size &&
                x.size == low.size &&
                x.size == closing.size
    }
}