// Bless the LORD oh my soul
package com.den.steward.ui.components.charts

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.den.steward.ui.components.charts.collections.CandlestickDataCollection
import com.den.steward.ui.components.charts.marker.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberCandlestickCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.candlestickSeries
import com.patrykandpatrick.vico.core.common.Fill

@Composable
fun VicoCandlestick(
    modifier: Modifier = Modifier,
    candlestickDataCollection: CandlestickDataCollection,
    xValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    yValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    markerFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    placeholderValueSize: Int = 6
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val candlestick = rememberCandlestickCartesianLayer()

    val chart = rememberCartesianChart(
        candlestick,
        marker = rememberMarker(valueFormatter = { _, value ->
            markerFormatter(value[0].x)
        }),
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            valueFormatter = { _, value, _ -> xValueFormatter(value) }
        ),
        startAxis = VerticalAxis.rememberStart(
            line = rememberLineComponent(Fill.Transparent),
            title = "X",
            valueFormatter = { _, value, _ -> yValueFormatter(value) }
        )
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {

            if (!(candlestickDataCollection.allNotEmpty() && candlestickDataCollection.allHasSameSize())) {
                return@runTransaction
            }

            candlestickSeries(
                x = candlestickDataCollection.x,
                opening = candlestickDataCollection.opening,
                high = candlestickDataCollection.high,
                low = candlestickDataCollection.low,
                closing = candlestickDataCollection.closing,
            )
        }
    }

    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = modifier
            .height(280.dp),
    )
}
