// Glory Be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.components.charts.DonutChart
import com.den.steward.ui.components.charts.DonutChartData
import com.den.steward.ui.components.charts.collections.DonutChartDataCollection

@Composable
fun OverviewDonutChart(
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