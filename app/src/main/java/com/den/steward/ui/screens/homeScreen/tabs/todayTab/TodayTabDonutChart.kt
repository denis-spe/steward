// Glory be to the LORD of hosts
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.charts.DonutChart
import com.den.steward.ui.components.charts.DonutChartData
import com.den.steward.ui.components.charts.collections.DonutChartDataCollection
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TodayDonutChartView(
    donutChartData: List<DonutChartData>,
    donutChartCenterAmount: Double,
    chartSize: Dp = 350.dp,
    strokeWidth: Dp = 20.dp,
    strokeWidthSelected: Dp = 40.dp,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    DonutChart(
        modifier = Modifier,
        chartSize = chartSize,
        strokeWidth = strokeWidth,
        strokeWidthSelected = strokeWidthSelected,
        strokeCap = strokeCap,
        data = DonutChartDataCollection(donutChartData)
    ) { selectedItem ->
        if (selectedItem == null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text(
                text = "Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
            )
            Text(
                text = donutChartCenterAmount.formatToAmount(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            )
                }
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                )

                Text(
                    text = selectedItem.amount.formatToAmount(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
                )
            }
        }
    }
}

@Composable
fun TodayDonutChartErrorView(
    message: String
) {
    Text(
        text = message,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun TodayDonutChartShimmerView(
    chartSize: Dp = 350.dp,
    strokeWidth: Dp = 20.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    Box(
        modifier = Modifier
            .size(chartSize)
            .clip(CircleShape)
            .shimmerEffect(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(chartSize - (strokeWidth * 2))
                .clip(CircleShape)
                .background(backgroundColor)
        )
    }
}

@Composable
fun TodayDonutChartEmptyView(
    chartSize: Dp = 350.dp,
    strokeWidth: Dp = 20.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    val symbol = try {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).currency?.symbol ?: "$"
    } catch (_: Exception) {
        "$"
    }

    Box(
        modifier = Modifier
            .size(chartSize)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(chartSize - (strokeWidth * 2))
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Flow",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray
                )
                Text(
                    text = "$symbol 0.0",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray
                )
            }
        }
    }
}