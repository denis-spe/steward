// Bless be the name of LORD GOD
package com.den.steward.ui.componentExtenison

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DottedHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    thickness: Dp = 2.dp,
    dotSpacing: Dp = 4.dp,
) {
    Canvas(modifier = modifier) {
        val strokeWidthPx = thickness.toPx()
        val gapWidthPx = dotSpacing.toPx()

        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(0f, strokeWidthPx + gapWidthPx),
                phase = 0f
            )
        )
    }
}