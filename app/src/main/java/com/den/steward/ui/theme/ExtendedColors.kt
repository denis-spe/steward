// Bless be the LORD GOD of hosts
package com.den.steward.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

@Immutable
data class ExtendedColors(
    val text: Color,
    val background: Color,
    val primary: Color,
    val secondary: Color
) {
    val lightPrimary: Color = primary.copy(alpha = 0.1f).compositeOver(background)
    val lightSecondary: Color = secondary.copy(alpha = 0.3f).compositeOver(background)
}
