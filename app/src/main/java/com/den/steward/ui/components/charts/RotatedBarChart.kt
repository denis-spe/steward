// Glory be to the LORD of hosts
package com.den.steward.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import io.androidpoet.drafter.bars.BarChartDataRenderer
import io.androidpoet.drafter.theme.DrafterColors
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * A modified version of Drafter's BarChart that supports label rotation.
 */
@Composable
fun RotatedBarChart(
    renderer: BarChartDataRenderer,
    modifier: Modifier = Modifier,
    labelRotation: Float = 0f,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    animate: Boolean = true,
) {
    val textMeasurer = rememberTextMeasurer()
    val animationProgress = remember { Animatable(if (animate) 0f else 1f) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(animate) {
        if (animate) {
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing,
                ),
            )
        }
    }

    val labels = renderer.getLabels()
    val barsPerGroup = renderer.barsPerGroup()

    val labelStyle = TextStyle(
        fontSize = 12.sp,
        color = if (isSystemInDarkTheme) DrafterColors.LabelDark else DrafterColors.LabelLight,
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .hoverable(interactionSource),
    ) {
        if (size.width < 1f || size.height < 1f) return@Canvas

        // Measure real label sizes up front so the layout can reserve the
        // exact vertical space rotated text needs at this specific angle.
        val labelSize = measureMaxLabelSize(textMeasurer, labels, labelStyle)

        val (_, chartHeight, chartWidth, chartTop, chartBottom, chartLeft) =
            calculateChartDimensions(
                width = size.width,
                height = size.height,
                labelRotation = labelRotation,
                maxLabelWidth = labelSize.width,
                maxLabelHeight = labelSize.height,
            )

        drawAxes(chartLeft, chartTop, chartBottom, chartWidth, isSystemInDarkTheme)

        val maxValue = renderer.calculateMaxValue()
        val (barWidth, groupSpacing) = renderer.calculateBarAndSpacing(
            chartWidth = chartWidth,
            dataSize = labels.size,
            barsPerGroup = barsPerGroup,
        )

        drawYAxisLabels(
            textMeasurer = textMeasurer,
            maxValue = maxValue,
            left = chartLeft,
            top = chartTop,
            bottom = chartBottom,
            isSystemInDarkTheme = isSystemInDarkTheme,
        )

        drawBars(
            renderer = renderer,
            labels = labels,
            chartLeft = chartLeft,
            chartBottom = chartBottom,
            chartHeight = chartHeight,
            barWidth = barWidth,
            groupSpacing = groupSpacing,
            barsPerGroup = barsPerGroup,
            maxValue = maxValue,
            animationProgress = animationProgress.value,
        )

        drawXAxisLabels(
            textMeasurer = textMeasurer,
            labels = labels,
            chartLeft = chartLeft,
            chartBottom = chartBottom,
            barWidth = barWidth,
            barsPerGroup = barsPerGroup,
            groupSpacing = groupSpacing,
            style = labelStyle,
            labelRotation = labelRotation,
        )
    }
}

private data class LabelSize(val width: Float, val height: Float)

private fun measureMaxLabelSize(
    textMeasurer: TextMeasurer,
    labels: List<String>,
    style: TextStyle,
): LabelSize {
    if (labels.isEmpty()) return LabelSize(0f, 0f)
    var maxW = 0f
    var maxH = 0f
    labels.forEach {
        val measured = textMeasurer.measure(it, style)
        if (measured.size.width > maxW) maxW = measured.size.width.toFloat()
        if (measured.size.height > maxH) maxH = measured.size.height.toFloat()
    }
    return LabelSize(maxW, maxH)
}

private fun calculateChartDimensions(
    width: Float,
    height: Float,
    labelRotation: Float,
    maxLabelWidth: Float,
    maxLabelHeight: Float,
): ChartDimensions {
    val padding = width * 0.15f
    val angleRad = Math.toRadians(labelRotation.toDouble())

    // Real footprint of the rotated text's bounding box, vertically.
    val rotatedLabelHeight = (
            maxLabelWidth * abs(sin(angleRad)) + maxLabelHeight * abs(cos(angleRad))
            ).toFloat()

    val bottomReserve = (rotatedLabelHeight + 28f) // padding for tick gap
        .coerceIn(height * 0.12f, height * 0.45f) // sane floor/ceiling

    val chartHeight = height - (height * 0.15f) - bottomReserve
    return ChartDimensions(
        chartPadding = padding,
        chartHeight = chartHeight,
        chartWidth = width - (padding * 2),
        chartTop = height * 0.15f,
        chartBottom = height * 0.15f + chartHeight,
        chartLeft = padding,
    )
}

private fun DrawScope.drawYAxisLabels(
    textMeasurer: TextMeasurer,
    maxValue: Float,
    left: Float,
    top: Float,
    bottom: Float,
    isSystemInDarkTheme: Boolean,
) {
    val style = TextStyle(
        fontSize = 12.sp,
        color = if (isSystemInDarkTheme) DrafterColors.LabelDark else DrafterColors.LabelLight,
    )
    val gridColor = if (isSystemInDarkTheme) DrafterColors.GridDark else DrafterColors.GridLight
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), phase = 0f)
    val steps = 5
    val stepValue = maxValue / steps

    for (i in 0..steps) {
        val value = stepValue * i
        val yPosition = bottom - ((value / maxValue) * (bottom - top))
        val label = ((value * 10).toInt() / 10f).toString()

        drawLine(
            color = gridColor,
            start = Offset(left, yPosition),
            end = Offset(size.width, yPosition),
            strokeWidth = 1f,
            pathEffect = dashEffect,
        )

        val measured = textMeasurer.measure(label, style)
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = style,
            topLeft = Offset(
                x = (left - measured.size.width - 8f),
                y = yPosition - (measured.size.height / 2),
            ),
        )
    }
}

private fun DrawScope.drawBars(
    renderer: BarChartDataRenderer,
    labels: List<String>,
    chartLeft: Float,
    chartBottom: Float,
    chartHeight: Float,
    barWidth: Float,
    groupSpacing: Float,
    barsPerGroup: Int,
    maxValue: Float,
    animationProgress: Float,
) {
    var currentLeft = chartLeft + groupSpacing
    val groupWidth = barWidth * barsPerGroup
    labels.forEachIndexed { index, _ ->
        renderer.drawBars(
            drawScope = this,
            index = index,
            left = currentLeft,
            barWidth = barWidth,
            groupSpacing = groupSpacing,
            chartBottom = chartBottom,
            chartHeight = chartHeight,
            maxValue = maxValue,
            animationProgress = animationProgress,
        )
        currentLeft += groupWidth + groupSpacing
    }
}

/** Truncates [label] with an ellipsis so it fits within [maxWidth]. */
private fun truncateLabel(
    label: String,
    maxWidth: Float,
    textMeasurer: TextMeasurer,
    style: TextStyle,
): String {
    if (maxWidth <= 0f) return label
    if (textMeasurer.measure(label, style).size.width <= maxWidth) return label

    var truncated = label
    while (truncated.isNotEmpty()) {
        val candidate = "$truncated…"
        if (textMeasurer.measure(candidate, style).size.width <= maxWidth) {
            return candidate
        }
        truncated = truncated.dropLast(1)
    }
    return "…"
}

private fun DrawScope.drawXAxisLabels(
    textMeasurer: TextMeasurer,
    labels: List<String>,
    chartLeft: Float,
    chartBottom: Float,
    barWidth: Float,
    barsPerGroup: Int,
    groupSpacing: Float,
    style: TextStyle,
    labelRotation: Float,
) {
    val groupWidth = barWidth * barsPerGroup
    val isRotated = labelRotation != 0f
    val angleRad = Math.toRadians(labelRotation.toDouble())

    // Horizontal footprint each rotated label actually occupies on screen —
    // used to decide if labels need thinning, at any angle (not just 0°).
    val skipFactor = if (labels.isNotEmpty()) {
        val available = groupWidth + groupSpacing
        val maxHorizontalExtent = labels.maxOf { label ->
            val measured = textMeasurer.measure(label, style)
            abs(measured.size.width * cos(angleRad)) +
                    abs(measured.size.height * sin(angleRad))
        }.toFloat()
        if (available <= 0f) 1 else (maxHorizontalExtent / available).toInt().coerceAtLeast(1)
    } else {
        1
    }

    var currentLeft = chartLeft + groupSpacing

    labels.forEachIndexed { index, rawLabel ->
        val centerX = currentLeft + (groupWidth / 2)
        val labelY = chartBottom + 12f // gap between axis and label

        if (index % skipFactor == 0) {
            val label = if (!isRotated) {
                truncateLabel(rawLabel, groupWidth + groupSpacing - 8f, textMeasurer, style)
            } else {
                rawLabel
            }
            val measured = textMeasurer.measure(label, style)

            if (isRotated) {
                // The pivot MUST be the bar's true center on screen — it's the
                // fixed point rotation happens around. We draw the unrotated
                // text so its right edge sits exactly at that pivot
                // (text-anchor: end), then rotate around the same point.
                // Previously the pivot itself was shifted by +width/2, which
                // made every label drift off its bar by an amount that
                // depended on that label's own text length — the bug.
                rotate(degrees = labelRotation, pivot = Offset(centerX, labelY)) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        style = style,
                        topLeft = Offset(
                            x = centerX - measured.size.width,
                            y = labelY,
                        ),
                    )
                }
            } else {
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = style,
                    topLeft = Offset(
                        x = centerX - (measured.size.width / 2),
                        y = labelY,
                    ),
                )
            }
        }

        currentLeft += groupWidth + groupSpacing
    }
}

private fun DrawScope.drawAxes(
    left: Float,
    top: Float,
    bottom: Float,
    width: Float,
    isSystemInDarkTheme: Boolean,
) {
    val axisColor = if (isSystemInDarkTheme) DrafterColors.GridDark else DrafterColors.GridLight
    drawLine(
        color = axisColor,
        start = Offset(left, bottom),
        end = Offset(left + width, bottom),
        strokeWidth = 1.5f,
    )
}

private data class ChartDimensions(
    val chartPadding: Float,
    val chartHeight: Float,
    val chartWidth: Float,
    val chartTop: Float,
    val chartBottom: Float,
    val chartLeft: Float,
)