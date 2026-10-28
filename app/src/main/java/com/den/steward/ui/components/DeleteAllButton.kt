// Praise the LORD of hosts for the LORD is good and his mercy endure forever
package com.den.steward.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

@Composable
fun DeleteAllButton(
    visible: Boolean = true,
    title: String = "Delete All",
    subtitle: String = "Delete all transactions",
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors()
                .copy(
                    containerColor = Color(0xFFF62229).copy(0.2f).compositeOver(
                        MaterialTheme.colorScheme.background
                    ),
                    contentColor = Color(0xFFF62229)
                ),
            border = BorderStroke(2.dp, Color(0xFFF62229))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "DeleteForever"
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFF62229)
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}