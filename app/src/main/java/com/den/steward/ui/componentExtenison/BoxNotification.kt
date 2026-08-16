// Bless be the LORD GOD of hosts
package com.den.steward.ui.componentExtenison

import android.app.Notification
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.den.steward.backend.states.AuthState

@Composable
fun BoxScope.BoxNotification(
    visible: Boolean,
    notificationText: String?,
    isSuccessMessage: Boolean = false
){
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxWidth()
            .align(Alignment.TopCenter),
        exit = slideOutVertically(),
        enter = slideInVertically() + fadeIn(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            notificationText?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(0.7f)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSuccessMessage) Color(0xFF4CAF50)
                        else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}