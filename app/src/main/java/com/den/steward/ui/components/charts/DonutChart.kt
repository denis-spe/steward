// Hear oh Israel, The LORD our GOD, the LORD is one,
// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self.

// Code by eozsahin1993
package com.den.steward.ui.components.charts

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.den.steward.ui.components.charts.collections.DonutChartDataCollection
import kotlin.math.pow
import kotlin.math.sqrt

private const val TOTAL_ANGLE = 360.0f

private data class DonutStroke(
    val strokeSizeUnselected: Dp = 40.dp,
    val strokeSizeSelected: Dp = 60.dp
)


private data class DrawingAngles(val start: Float, val end: Float)

private fun DrawingAngles.isInsideAngle(angle: Float) =
    angle > this.start && angle < this.start + this.end

private class DonutChartState(
    val state: State = State.Unselected,
    val donutStroke: DonutStroke = DonutStroke()
) {
    val stroke: Dp
        get() = when (state) {
            State.Selected -> donutStroke.strokeSizeSelected
            State.Unselected -> donutStroke.strokeSizeUnselected
        }

    enum class State {
        Selected, Unselected
    }
}

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    chartSize: Dp = 350.dp,
    data: DonutChartDataCollection,
    gapPercentage: Float = 0.04f,
    strokeWidth: Dp = 20.dp,
    strokeWidthSelected: Dp = 40.dp,
    strokeCap: StrokeCap = StrokeCap.Butt,
    selectionView: @Composable (selectedItem: DonutChartData?) -> Unit = {},
) {

    var selectedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(data.items.size) {
        if (selectedIndex >= data.items.size) {
            selectedIndex = -1
        }
    }

    val animationTargetState = data.items.indices.map {
        remember {
            mutableStateOf(
                DonutChartState(
                    donutStroke = DonutStroke(strokeWidth, strokeWidthSelected)
                )
            )
        }
    }
    val animValues = data.items.indices.map {
        animateDpAsState(
            targetValue = animationTargetState[it].value.stroke,
            animationSpec = TweenSpec(700)
        )
    }
    val anglesList: MutableList<DrawingAngles> = remember { mutableListOf() }
    val gapAngle = data.calculateGapAngle(gapPercentage)
    var center = Offset(0f, 0f)

    var animationProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(data) {
        animationProgress = 1f
    }
    val progress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "DonutChartAnimation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(chartSize)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            handleCanvasTap(
                                center = center,
                                tapOffset = tapOffset,
                                anglesList = anglesList,
                                currentSelectedIndex = selectedIndex,
                                currentStrokeValues = animationTargetState.map { it.value.stroke.toPx() },
                                onItemSelected = { index ->
                                    selectedIndex = index
                                    if (index in animationTargetState.indices) {
                                        animationTargetState[index].value = DonutChartState(
                                            DonutChartState.State.Selected,
                                            donutStroke = DonutStroke(
                                                strokeWidth,
                                                strokeWidthSelected
                                            )
                                        )
                                    }
                                },
                                onItemDeselected = { index ->
                                    if (index in animationTargetState.indices) {
                                        animationTargetState[index].value = DonutChartState(
                                            DonutChartState.State.Unselected,
                                            donutStroke = DonutStroke(
                                                strokeWidth,
                                                strokeWidthSelected
                                            )
                                        )
                                    }
                                },
                                onNoItemSelected = {
                                    selectedIndex = -1
                                }
                            )
                        }
                    )
                },
            onDraw = {
                val maxStrokeWidth = maxOf(strokeWidth, strokeWidthSelected).toPx()
                center = this.center
                anglesList.clear()
                var lastAngle = 0f
                data.items.forEachIndexed { ind, item ->
                    val sweepAngle = data.findSweepAngle(ind, gapPercentage)
                    anglesList.add(DrawingAngles(lastAngle, sweepAngle))
                    val currentStrokeWidth = animValues[ind].value.toPx()
                    drawArc(
                        color = item.color,
                        startAngle = lastAngle * progress,
                        sweepAngle = sweepAngle * progress,
                        useCenter = false,
                        topLeft = Offset(maxStrokeWidth / 2, maxStrokeWidth / 2),
                        style = Stroke(currentStrokeWidth, cap = strokeCap),
                        size = Size(
                            size.width - maxStrokeWidth,
                            size.height - maxStrokeWidth
                        )
                    )
                    lastAngle += sweepAngle + gapAngle
                }
            }
        )
        selectionView(data.items.getOrNull(selectedIndex))
    }
}


private fun handleCanvasTap(
    center: Offset,
    tapOffset: Offset,
    anglesList: List<DrawingAngles>,
    currentSelectedIndex: Int,
    currentStrokeValues: List<Float>,
    onItemSelected: (Int) -> Unit = {},
    onItemDeselected: (Int) -> Unit = {},
    onNoItemSelected: () -> Unit = {},
) {
    val normalized = tapOffset.findNormalizedPointFromTouch(center)
    val touchAngle =
        calculateTouchAngleAccordingToCanvas(center, normalized)
    val distance = findTouchDistanceFromCenter(center, normalized)

    var selectedIndex = -1
    var newDataTapped = false

    anglesList.forEachIndexed { ind, angle ->
        if (ind < currentStrokeValues.size) {
            val stroke = currentStrokeValues[ind]
            if (angle.isInsideAngle(touchAngle)) {
                if (distance > (center.x - stroke) &&
                    distance < (center.x)
                ) { // since it's a square center.x or center.y will be the same
                    selectedIndex = ind
                    newDataTapped = true
                }
            }
        }
    }

    if (selectedIndex >= 0) {
        onItemSelected(selectedIndex)
    }
    if (currentSelectedIndex >= 0) {
        onItemDeselected(currentSelectedIndex)
        if (currentSelectedIndex == selectedIndex || !newDataTapped) {
            onNoItemSelected()
        }
    }
}

/**
 * Find the distance based on two points in a graph. Calculated using the pythagorean theorem.
 */
private fun findTouchDistanceFromCenter(center: Offset, touch: Offset) =
    sqrt((touch.x - center.x).pow(2) + (touch.y - center.y).pow(2))

/**
 * The touch point start from Canvas top left which ranges from (0,0) -> (canvas.width, canvas.height).
 * We need to normalize this point so that it's based on the canvas center instead.
 */
private fun Offset.findNormalizedPointFromTouch(canvasCenter: Offset) =
    Offset(this.x, canvasCenter.y + (canvasCenter.y - this.y))

/**
 * Calculate the touch angle based on the canvas center. Then adjust the angle so that
 * drawing starts from the 4th quadrant instead of the first.
 */
private fun calculateTouchAngleAccordingToCanvas(
    canvasCenter: Offset,
    normalizedPoint: Offset
): Float {
    val angle = calculateTouchAngleInDegrees(canvasCenter, normalizedPoint)
    return adjustAngleToCanvas(angle).toFloat()
}

/**
 * Calculate touch angle in radian using atan2(). Afterwards, convert the radian to degrees to be
 * compared to other data points.
 */
private fun calculateTouchAngleInDegrees(canvasCenter: Offset, normalizedPoint: Offset): Double {
    val touchInRadian = kotlin.math.atan2(
        normalizedPoint.y - canvasCenter.y,
        normalizedPoint.x - canvasCenter.x
    )
    return touchInRadian * -180 / Math.PI // Convert radians to angle in degrees
}

/**
 * Start from 4th quadrant going to 1st quadrant, degrees ranging from 0 to 360
 */
private fun adjustAngleToCanvas(angle: Double) = (angle + TOTAL_ANGLE) % TOTAL_ANGLE

/**
 * Calculate the gap width between the arcs based on [gapPercentage]. The percentage is applied
 * to the average count to determine the width in pixels.
 */
private fun DonutChartDataCollection.calculateGap(gapPercentage: Float): Float {
    if (this.items.isEmpty()) return 0f

    return (this.totalAmount / this.items.size) * gapPercentage
}

/**
 * Returns the total data points including the individual gap widths indicated by the
 * [gapPercentage].
 */
private fun DonutChartDataCollection.getTotalAmountWithGapIncluded(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    return this.totalAmount + (this.items.size * gap)
}

/**
 * Calculate the sweep angle of an arc including the gap as well. The gap is derived based
 * on [gapPercentage].
 */
private fun DonutChartDataCollection.calculateGapAngle(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    val totalAmountWithGap = this.getTotalAmountWithGapIncluded(gapPercentage)

    return (gap / totalAmountWithGap) * TOTAL_ANGLE
}

/**
 * Returns the sweep angle of a given point in the [DonutChartDataCollection]. This calculations
 * takes the gap between arcs into the account.
 */
private fun DonutChartDataCollection.findSweepAngle(
    index: Int,
    gapPercentage: Float
): Float {
    val amount = items[index].amount
    val gap = this.calculateGap(gapPercentage)
    val totalWithGap = getTotalAmountWithGapIncluded(gapPercentage)
    val gapAngle = this.calculateGapAngle(gapPercentage)
    return ((((amount + gap) / totalWithGap) * TOTAL_ANGLE)) - gapAngle
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    val data = DonutChartDataCollection(
        listOf(
            DonutChartData(100f, Color.Red, "Red"),
            DonutChartData(200f, Color.Blue, "Blue"),
            DonutChartData(300f, Color.Green, "Green")
        )
    )
    DonutChart(data = data)
}

