// Glory be to the LORD of hosts
package com.den.steward.ui.screens.homeScreen.transactionCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.charts.DonutChart
import com.den.steward.ui.components.charts.DonutChartData
import com.den.steward.ui.components.charts.collections.DonutChartDataCollection

@Composable
fun TransactionDonutChartView(
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
            Text(
                text = donutChartCenterAmount.formatToAmount(),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedItem.title
                )

                Text(
                    text = selectedItem.amount.formatToAmount()
                )
            }
        }
    }
}

@Composable
fun TransactionDonutChartPercentView(
    donutChartData: List<DonutChartData>,
    donutChartCenterAmount: Double,
    chartSize: Dp = 350.dp,
    strokeWidth: Dp = 20.dp,
    strokeWidthSelected: Dp = 40.dp,
) {
    DonutChart(
        modifier = Modifier,
        chartSize = chartSize,
        strokeWidth = strokeWidth,
        strokeCap = StrokeCap.Round,
        strokeWidthSelected = strokeWidthSelected,
        data = DonutChartDataCollection(donutChartData)
    ) { selectedItem ->
        if (selectedItem == null) {
            Text(
                text = "${donutChartCenterAmount.toInt()}%",
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedItem.title
                )

                Text(
                    text = selectedItem.amount.formatToAmount()
                )
            }
        }
    }
}

@Composable
fun TransactionDonutChartErrorView(
    message: String
) {
    Text(
        text = message,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun TransactionDonutChartShimmerView(
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
fun TransactionDonutChartEmptyView(
    chartSize: Dp = 350.dp,
    strokeWidth: Dp = 20.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    Box(
        modifier = Modifier
            .size(chartSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(chartSize - (strokeWidth * 2))
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            Text(
                text = "No data",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}