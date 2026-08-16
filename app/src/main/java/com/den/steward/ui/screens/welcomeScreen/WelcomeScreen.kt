package com.den.steward.ui.screens.welcomeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.WelcomeViewModel
import com.den.steward.ui.componentExtenison.BoxNotification
import com.den.steward.ui.screens.screenManager.HomeRouter
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun WelcomeScreen(
    backStack: NavBackStack<NavKey>,
    welcomeViewModel: WelcomeViewModel
) {
    // Observe the userState
    val userState by welcomeViewModel.userState.collectAsStateWithLifecycle()

    val serverMessage = if (userState is AuthState.Error) {
        (userState as AuthState.Error).message
    } else {
        null
    }

    val isLoading = welcomeViewModel.isLoading.collectAsStateWithLifecycle()

    // Navigation logic handled in LaunchedEffect to avoid side effects during composition
    LaunchedEffect(userState) {
        if (userState is AuthState.Authenticated) {
            backStack.clear()
            backStack.add(HomeRouter)
        } else if (userState is AuthState.Error) {
            delay(3000.milliseconds) // Delay for 3 seconds
            welcomeViewModel.updateAuthState(AuthState.NotAuthenticated)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(padding),
        ) {
            BoxNotification(
                visible = serverMessage != null,
                notificationText = serverMessage
            )

            WelcomeContent(
                welcomeViewModel = welcomeViewModel,
                backStack = backStack
            )

            if (isLoading.value) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    trackColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

        }
    }
}

