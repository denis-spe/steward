// Bless be the LORD GOD of hosts
package com.den.steward.ui.screens.componentExtenison

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.den.steward.backend.states.AuthState
import com.den.steward.ui.screens.welcomeScreen.ShowServerMessage

@Composable
fun BoxScope.BoxNotification(
    visible: Boolean,
    serverErrorMessage: String?
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
            serverErrorMessage?.let {
                ShowServerMessage(
                    serverMessage = it
                )
            }
        }
    }
}