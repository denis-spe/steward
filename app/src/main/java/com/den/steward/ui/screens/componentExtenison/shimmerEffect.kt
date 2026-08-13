// Love the LORD your GOD with all your soul and with all your mind and with all your might
// and love your neighbor as your self
package com.den.steward.ui.screens.componentExtenison

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize

/**
 * A professional shimmer effect modifier that applies a diagonal light sweep to any component.
 *
 * @param colors The list of colors to use in the shimmer gradient.
 * @param duration The duration of one full shimmer cycle in milliseconds.
 */
fun Modifier.shimmerEffect(
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    ),
    duration: Int = 1300,
): Modifier = composed {
    var size by remember { mutableStateOf(Size.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer_transition")

    // We animate across a range larger than the component width to ensure
    // the gradient fully enters and exits the view for a clean loop.
    val translateAnim by transition.animateFloat(
        initialValue = -2 * (if (size.width > 0f) size.width else 1000f),
        targetValue = 2 * (if (size.width > 0f) size.width else 1000f),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    this.onGloballyPositioned {
        size = it.size.toSize()
    }.background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(translateAnim, translateAnim),
            end = Offset(translateAnim + size.width, translateAnim + size.height)
        )
    )
}
