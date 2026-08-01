// Glory be the name of LORD GOD
package com.den.steward.ui.screens.authScreen.authComponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AuthButton(
    onClick: () -> Unit,
    text: String,
    isError: Boolean
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(2.dp,
            if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}