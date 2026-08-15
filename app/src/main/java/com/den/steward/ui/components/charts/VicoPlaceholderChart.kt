// Praise be the LORD, for the LORD is good and his mercy endures forever
package com.den.steward.ui.components.charts

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill

@Composable
fun VicoPlaceholderChart(
    modifier: Modifier = Modifier,
    chartDataCollection: ChartDataCollection,
    xValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    yValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val chartData = chartDataCollection.chartData

    var lineLayer = rememberLineCartesianLayer()

    // Create and configure the line layer only when we have data.
    if (chartDataCollection.allAreNotEmpty()) {
        lineLayer = rememberLineCartesianLayer(
            lineProvider = { index, _ ->

                LineCartesianLayer.Line(
                    fill = LineCartesianLayer.LineFill.single(
                        Fill(Color.Transparent.toArgb())
                    ),
                    stroke = LineCartesianLayer.LineStroke.continuous(
                        cap = StrokeCap.Round,
                        thickness = 2.dp
                    ),
                )
            }
        )
    }

    val chart = rememberCartesianChart(
        lineLayer,
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            valueFormatter = { _, value, _ -> xValueFormatter(value) }
        ),
        startAxis = VerticalAxis.rememberStart(
            line = rememberLineComponent(Fill.Transparent),
            itemPlacer = VerticalAxis.ItemPlacer.count({ 2 }),
            valueFormatter = { _, value, _ -> yValueFormatter(value) }
        ),
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {

            if (!(chartDataCollection.allAreNotEmpty())) {
                return@runTransaction
            }

            lineSeries {
                chartData.forEach { lineData ->
                    series(
                        x = lineData.x,
                        y = lineData.y
                    )
                }
            }

        }
    }

    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = modifier
            .height(280.dp)
    )
}
