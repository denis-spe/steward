package com.den.steward.ui.components.charts

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class DonutChartData(
    val amount: Float = 0.0f,
    val color: Color = Color.Unspecified,
    val title: String = "",
)