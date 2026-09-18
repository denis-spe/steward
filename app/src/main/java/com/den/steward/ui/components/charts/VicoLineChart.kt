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
    markerFormatter: ((x: Double, y: Double) -> CharSequence)? = null,
    horizontalItemPlacer: HorizontalAxis.ItemPlacer = remember { HorizontalAxis.ItemPlacer.aligned() },
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)
    val chartData = chartDataCollection.chartData

    val vicoMarkerFormatter = remember(xValueFormatter, yValueFormatter, markerFormatter, chartData) {
        DefaultCartesianMarker.ValueFormatter { context, targets ->
            val primaryTarget = targets.firstOrNull() as? LineCartesianLayerMarkerTarget
            val points = primaryTarget?.points ?: return@ValueFormatter ""

            if (points.isEmpty()) return@ValueFormatter ""

            val sb = StringBuilder()

            points.forEachIndexed { index, point ->
                val entry = point.entry
                val seriesLabel = chartData.find { it.color.toArgb() == point.color }?.label

                val xStr = xValueFormatter(entry.x)
                val yStr = yValueFormatter(entry.y)
                val label = seriesLabel?.let { "$it: " } ?: ""

                val formattedValue = markerFormatter?.invoke(entry.x, entry.y)
                    ?: "$xStr | $label$yStr"

                sb.append(formattedValue)
                if (index < points.size - 1) sb.append("\n")
            }
            sb
        }
    }

    val marker = rememberMarker(valueFormatter = vicoMarkerFormatter)

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = remember(chartData, fillArea, lineType) {
            LineCartesianLayer.LineProvider.series(
                chartData.map { lineColor ->
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
    )
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

    val chart = rememberCartesianChart(
        lineLayer,
        marker = marker,
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            itemPlacer = horizontalItemPlacer,
            valueFormatter = { _, value, _ ->
                xValueFormatter(value).let { if (it.isEmpty()) " " else it }
            }
        ),
        startAxis = VerticalAxis.rememberStart(
            line = rememberLineComponent(Fill.Transparent),
            title = "Y",
            valueFormatter = { _, value, _ ->
                yValueFormatter(value).let { if (it.isEmpty()) " " else it }
            },
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
