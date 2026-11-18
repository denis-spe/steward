//Glory be to the name of the LORD GOD
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.den.steward.helper.formatToAmount
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle

@Composable
fun ComposeLineChart(
    data: List<Line>,
    labels: List<String> = emptyList(),
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
        enabled = true,
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

    val effectiveLabels = labelProperties.labels.ifEmpty { labels }
    val maxPoints = effectiveLabels.size.takeIf { it > 0 }
        ?: data.maxOfOrNull { it.values.size }
        ?: 0

    val baseSlotWidth = 48.dp
    val scaledSlotWidth = baseSlotWidth * zoomScale
    val extraEndBuffer = 36.dp
    val chartWidth = (scaledSlotWidth * maxPoints + extraEndBuffer)

    val finalLabelProperties = if (labelProperties.labels.isEmpty() && labels.isNotEmpty()) {
        labelProperties.copy(labels = labels)
    } else {
        labelProperties
    }
    val selectedPopupLine = remember { mutableStateOf<Line?>(null) }

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
            LineChart(
                modifier = Modifier
                    .width(effectiveWidth)
                    .padding(end = 24.dp),
                data = data,
                labelProperties = finalLabelProperties,
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
                        val label = data.getOrNull(popup.dataIndex)?.label ?: ""
                        if (popup.value != 0.0) {
                            "$amount | $label"
                        } else {
                            "0"
                        }
                    },
                    containerColor = Color.Black.copy(0.8f),
                    mode = PopupProperties.Mode.PointMode(),
                    duration = 4200
                ),
                dotsProperties = DotProperties(
                    enabled = true,
                    radius = 3.dp,
                    strokeWidth = 4.dp,
                    color = SolidColor(Color.DarkGray),
                    strokeColor = SolidColor(Color.Black.copy(0.2f)),
                    confirmDraw = { dot ->
                        true
                    }
                )
            )
        }
    }
}
