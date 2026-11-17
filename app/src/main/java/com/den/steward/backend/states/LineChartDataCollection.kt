// Glory be to the name of the LORD GOD
package com.den.steward.backend.states

import androidx.compose.runtime.Stable
import ir.ehsannarmani.compose_charts.models.Line
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class LineChartDataCollection(
    val xAxisLabels: ImmutableList<String> = persistentListOf(),
    val lineData: ImmutableList<Line> = persistentListOf()
)
