// Love the LORD your GOD with all your soul and with all your heart
// and with all your might and love your neighbor as yourself
package com.den.steward.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.den.steward.R

@Composable
internal fun Footer() {
    Column (
        modifier = Modifier.fillMaxWidth()
            .testTag(stringResource(R.string.footer)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(40.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle( style = SpanStyle(
                    fontFamily = FontFamily(Font(R.font.special_font, weight = FontWeight.Bold))
                ) ) {
                    append(stringResource(R.string.gloryBeTOGOD) + "\n")
                }
                append(stringResource(R.string.footer))
                                        },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

