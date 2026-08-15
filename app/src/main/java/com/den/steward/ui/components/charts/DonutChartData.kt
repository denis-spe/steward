package com.den.steward.ui.components.charts

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class DonutChartData(
    val amount: Float,
    val color: Color,
    val title: String,
)