// Hear oh Israel, The LORD our GOD, the LORD is one,
// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self.
package com.den.steward.ui.components.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import com.den.steward.ui.components.charts.marker.rememberMarker
import com.den.steward.ui.theme.StewardTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape

//@Composable
//fun VicoBarChart(
//    modifier: Modifier = Modifier,
//    chartDataCollection: ChartDataCollection,
//    thickness: Dp = 5.dp,
//    strokeThickness: Dp = 0.dp,
//    showLegend: Boolean = false,
//    xValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
//    yValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
//    markerFormatter: (x: Double, y: Double) -> CharSequence =
//        { x, y -> "$x, $y" },
//) {
//    val modelProducer = remember { CartesianChartModelProducer() }
//    val chartData = chartDataCollection.chartData
//    val textComponent = rememberTextComponent(color = StewardTheme.colors.onSurfaceText)
//    val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)
//
//    val markerFormatter = DefaultCartesianMarker.ValueFormatter { context, targets ->
//        // Get the first highlighted point's data
//        val primaryTarget = targets.firstOrNull() as? LineCartesianLayerMarkerTarget
//        val entry = primaryTarget?.points?.firstOrNull()?.entry
//
//        if (entry != null) {
//            // Format as "X: Value, Y: Value" or any layout you prefer
//            markerFormatter(entry.x, entry.y)
//        } else {
//            ""
//        }
//    }
//
//    val marker = rememberMarker(valueFormatter = markerFormatter)
//
//    val legend = rememberHorizontalLegend<CartesianMeasuringContext, CartesianDrawingContext>(
//        items = { extraStore -> // 'this' is the AdditionScope<LegendItem>
//            chartData.forEach { lineData ->
//                add(
//                    LegendItem(
//                        icon = ShapeComponent(
//                            fill = Fill(lineData.color.toArgb()),
//                            shape = Shape.Rectangle,
//                        ),
//                        labelComponent = textComponent,
//                        label = lineData.label ?: "",
//                    )
//                )
//            }
//        },
//        iconSize = 8.dp,
//        iconLabelSpacing = 4.dp,
//        columnSpacing = 16.dp,
//        padding = Insets(topDp = 16f) // Note: Insets usually take Floats in the core class
//    )
//
//    var columnLayer = rememberColumnCartesianLayer(columnCollectionSpacing = 5.dp)
//
//    // Create and configure the line layer only when we have data.
//    if (chartDataCollection.allAreNotEmpty()) {
//        columnLayer = rememberColumnCartesianLayer(
//            ColumnCartesianLayer.ColumnProvider.series(
//                chartData.map { lineData ->
//                    val color = lineData.color
//
//                    rememberLineComponent(
//                        fill = Fill(color.toArgb()),
//                        thickness = 104.dp,                 // <-- increase this
//                        strokeThickness = 0.dp
//                    )
//                },
//            ),
//            columnCollectionSpacing = 0.dp,
//            mergeMode = { ColumnCartesianLayer.MergeMode.Stacked }
//        )
//    }
//
//
//    val chart = rememberCartesianChart(
//        columnLayer,
//        marker = marker,
//        bottomAxis = HorizontalAxis.rememberBottom(
//            guideline = null,
//            valueFormatter = { _, value, _ -> xValueFormatter(value) }
//            // Removed itemPlacer for compatibility
//        ),
//        startAxis = VerticalAxis.rememberStart(
//            line = rememberLineComponent(
//                Fill.Transparent,
//                thickness = thickness,
//                strokeThickness = strokeThickness,
//            ),
//            title = "X",
//            valueFormatter = { _, value, _ -> yValueFormatter(value) },
//        ),
//        legend = if (showLegend) legend else null // Show legend
//    )
//
//    LaunchedEffect(Unit) {
//        modelProducer.runTransaction {
//            if (!(chartDataCollection.allAreNotEmpty())) {
//                return@runTransaction
//            }
//            columnSeries {
//                chartDataCollection.chartData.forEach { lineData ->
//                    series(
//                        x = lineData.x,
//                        y = lineData.y
//                    )
//                }
//            }
//        }
//    }
//
//
//    CartesianChartHost(
//        chart = chart,
//        modelProducer = modelProducer,
//        modifier = modifier
//            .height(280.dp),
//        zoomState = zoomState
//    )
//}

@Composable
fun VicoBarChart(
    modifier: Modifier = Modifier,
    chartDataCollection: ChartDataCollection,
    thickness: Dp = 12.dp,
    strokeThickness: Dp = 0.dp,
    showLegend: Boolean = false,
    xValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    yValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    markerFormatter: (value: Double) -> CharSequence =
        { value -> "$value" },
) {

    val modelProducer = remember { CartesianChartModelProducer() }
    val chartData = chartDataCollection.chartData

    val textComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface)
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)

    // ---------- Marker ----------
    val markerValueFormatter = DefaultCartesianMarker.ValueFormatter { _, targets ->
        val columnTarget =
            targets.filterIsInstance<ColumnCartesianLayerMarkerTarget>().firstOrNull()

        if (columnTarget != null && columnTarget.columns.isNotEmpty()) {
            val timeLabel = xValueFormatter(columnTarget.columns.first().entry.x)

            // Get labels for all series that have a non-zero value at this point
            val valuesLabel = columnTarget.columns
                .mapIndexedNotNull { index, column ->
                    if (column.entry.y <= 0) return@mapIndexedNotNull null
                    val seriesLabel = chartData.getOrNull(index)?.label?.let { "$it: " } ?: ""
                    seriesLabel + markerFormatter(column.entry.y)
                }
                .joinToString(", ")

            if (valuesLabel.isEmpty()) {
                "$timeLabel: 0"
            } else {
                "$timeLabel | $valuesLabel"
            }
        } else ""
    }

    val marker = rememberMarker(valueFormatter = markerValueFormatter)

    // ---------- Legend ----------
    val legend = rememberHorizontalLegend<CartesianMeasuringContext, CartesianDrawingContext>(
        items = {
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
        padding = Insets(topDp = 16f),
    )

    // ---------- Column Layer ----------
    val columnLayer =
        if (chartDataCollection.allAreNotEmpty()) {
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    chartData.map { lineData ->
                        rememberLineComponent(
                            fill = Fill(lineData.color.toArgb()),
                            thickness = thickness,     // column width
                            strokeThickness = strokeThickness,
                            shape = CorneredShape.rounded(50)
                        )
                    }
                ),
                mergeMode = { ColumnCartesianLayer.MergeMode.Stacked }
            )
        } else {
            rememberColumnCartesianLayer()
        }

    // ---------- Chart ----------
    val chart = rememberCartesianChart(
        columnLayer,
        marker = marker,

        // Use real X values (time in seconds)
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            tick = rememberAxisTickComponent(),
            valueFormatter = { _, value, _ ->
                xValueFormatter(value)
            }
        ),

        startAxis = VerticalAxis.rememberStart(
            line = rememberLineComponent(
                Fill.Transparent,
                thickness = thickness,
                strokeThickness = strokeThickness
            ),
            title = "Y",
            valueFormatter = { _, value, _ -> yValueFormatter(value) },
        ),

        legend = if (showLegend) legend else null
    )

    // ---------- Load Data ----------
    LaunchedEffect(chartDataCollection) {
        modelProducer.runTransaction {
            if (!chartDataCollection.allAreNotEmpty()) return@runTransaction

            columnSeries {
                chartDataCollection.chartData.forEach { lineData ->
                    series(
                        x = lineData.x,
                        y = lineData.y
                    )
                }
            }
        }
    }

    // ---------- Host ----------
    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()    // important for column size
            .height(280.dp),
        zoomState = zoomState
    )
}
