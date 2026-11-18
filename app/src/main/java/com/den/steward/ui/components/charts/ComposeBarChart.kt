// Glory be to the name of the LORD GOD
package com.den.steward.ui.components.charts

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.den.steward.helper.formatToAmount
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle

@Composable
fun ComposeBarChart(
    data: List<Bars>,
    cornerRadius: Bars.Data.Radius = Bars.Data.Radius.Rectangle(
        topRight = 10.dp,
        topLeft = 10.dp,
        bottomLeft = 10.dp,
        bottomRight = 10.dp
    ),
    labelProperties: LabelProperties = LabelProperties(
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface
        ),
        enabled = true
    ),
    labelHelperProperties: LabelHelperProperties = LabelHelperProperties(
        enabled = false,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface
        ),
        labelCountPerLine = 5
    ),
    gridProperties: GridProperties = GridProperties(
        xAxisProperties = GridProperties.AxisProperties(
            enabled = true,
            thickness = 0.dp,
            style = StrokeStyle.Dashed()
        ),
        yAxisProperties = GridProperties.AxisProperties(
            enabled = false,
            thickness = 0.dp,
        ),
    ),
    indicatorProperties: HorizontalIndicatorProperties = HorizontalIndicatorProperties(
        enabled = false,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface
        ),
        contentBuilder = {
            it.formatToAmount()
        },
    ),
    dividerProperties: DividerProperties = DividerProperties(
        xAxisProperties = LineProperties(
            enabled = false
        ),
        yAxisProperties = LineProperties(
            enabled = false
        )
    )
) {
    val scrollState = rememberScrollState()
    var zoomScale by remember { mutableFloatStateOf(1f) }

    val maxBarsInGroup = data.maxOfOrNull { it.values.size } ?: 1
    val groupBarWidth =
        (16.dp * maxBarsInGroup) + (6.dp * (maxBarsInGroup - 1).coerceAtLeast(0)) + 32.dp
    val scaledSlotWidth = groupBarWidth * zoomScale
    val extraEndBuffer = 32.dp
    val chartWidth = (scaledSlotWidth * data.size) + extraEndBuffer
    val selectedBar = remember { mutableStateOf<Bars.Data?>(null) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val effectiveWidth = max(maxWidth, chartWidth)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.5f, 3f)
                    }
                }
                .horizontalScroll(scrollState),
        ) {
            ColumnChart(
                modifier = Modifier
                    .width(effectiveWidth)
                    .padding(end = 24.dp),
                data = data,
                barProperties = BarProperties(
                    cornerRadius = cornerRadius,
                    spacing = (6.dp * zoomScale).coerceIn(2.dp, 12.dp),
                    thickness = (15.dp * zoomScale).coerceIn(8.dp, 40.dp)
                ),
                onBarClick = { barPopupData ->
                    selectedBar.value = barPopupData.bar
                },
                onBarLongClick = {
                    // Handle bar long click event here
                },
                labelProperties = labelProperties,
                labelHelperProperties = labelHelperProperties,
                gridProperties = gridProperties,
                indicatorProperties = indicatorProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    ),
                    contentBuilder = { popup ->
                        val amount = popup.value.formatToAmount()
                        "$amount | ${selectedBar.value?.label}"
                    },
                    containerColor = Color.Black.copy(0.8f),
                    mode = PopupProperties.Mode.PointMode(),
                    duration = 2200
                )
            )
        }
    }
}
