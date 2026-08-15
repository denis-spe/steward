// Hear oh Israel, The LORD our GOD, the LORD is one,
// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self.
package com.den.steward.ui.components.charts

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import com.den.steward.ui.components.charts.marker.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
fun VicoLineChart(
    modifier: Modifier = Modifier,
    chartDataCollection: ChartDataCollection,
    fillArea: Boolean = false,
    showLegend: Boolean = false,
    lineType: LineCartesianLayer.PointConnector = LineCartesianLayer.PointConnector.cubic(),
    xValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    yValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    markerFormatter: (x: Double, y: Double) -> CharSequence =
        { x, y -> "$x, $y" },
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)

    val vicoMarkerFormatter = remember(markerFormatter) {
        DefaultCartesianMarker.ValueFormatter { context, targets ->
            // Get the first highlighted point's data
            val primaryTarget = targets.firstOrNull() as? LineCartesianLayerMarkerTarget
            val entry = primaryTarget?.points?.firstOrNull()?.entry

            if (entry != null) {
                // Format as "X: Value, Y: Value" or any layout you prefer
                markerFormatter(entry.x, entry.y)
            } else {
                ""
            }
        }
    }

    val marker = rememberMarker(valueFormatter = vicoMarkerFormatter)


    val chartData = chartDataCollection.chartData

    var lineLayer = rememberLineCartesianLayer()
    val textComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface)

    val legend = rememberHorizontalLegend<CartesianMeasuringContext, CartesianDrawingContext>(
        items = { _ ->
            chartData.forEach { lineData ->
                add(
                    LegendItem(
                        icon = ShapeComponent(
                            fill = Fill(lineData.color.toArgb()),
                            shape = Shape.Rectangle,
                        ),
                        labelComponent = textComponent,
                        label = lineData.label ?: "",
                    )
                )
            }
        },
        iconSize = 8.dp,
        iconLabelSpacing = 4.dp,
        columnSpacing = 16.dp,
        padding = Insets(topDp = 16f)
    )

    // Create and configure the line layer only when we have data.
    if (chartDataCollection.allAreNotEmpty()) {
        lineLayer = rememberLineCartesianLayer(
            lineProvider = { index, _ ->

                val lineColor = chartData[index]

                val gradientFill = LineCartesianLayer.AreaFill.single(
                    Fill(
                        Brush.verticalGradient(
                            colors = listOf(
                                lineColor.color.copy(alpha = 0.35f),
                                lineColor.color.copy(alpha = 0f)
                            )
                        ).toShaderProvider()
                    )
                )

                LineCartesianLayer.Line(
                    fill = LineCartesianLayer.LineFill.single(
                        Fill(lineColor.color.toArgb())
                    ),
                    stroke = LineCartesianLayer.LineStroke.continuous(
                        cap = StrokeCap.Round,
                        thickness = 2.dp
                    ),
                    areaFill = if (fillArea) gradientFill else null,
                    pointConnector = lineType,
                )
            }
        )
    }

    val chart = rememberCartesianChart(
        lineLayer,
        marker = marker,
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            valueFormatter = { _, value, _ -> xValueFormatter(value) }
        ),
        startAxis = VerticalAxis.rememberStart(
            line = rememberLineComponent(Fill.Transparent),
            title = "X",
            valueFormatter = { _, value, _ -> yValueFormatter(value) },
            itemPlacer = VerticalAxis.ItemPlacer.count({
                chartDataCollection.chartData.size + 2
            }),
        ),
        legend = if (showLegend) legend else null // Always show legend
    )

    LaunchedEffect(chartData) {
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
            .height(280.dp),
        zoomState = zoomState
    )
}
