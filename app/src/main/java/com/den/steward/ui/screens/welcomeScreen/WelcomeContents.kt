// Great is the LORD of host, the GOD of Israel
package com.den.steward.ui.screens.welcomeScreen

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.WelcomeViewModel
import com.den.steward.helper.title
import com.den.steward.ui.components.Footer

@Composable
fun WelcomeContent(
    welcomeViewModel: WelcomeViewModel,
    backStack: NavBackStack<NavKey>
) {
    val context = LocalContext.current
    val onScroll = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(onScroll),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // App Title
            AppTitle()

            // Description
            WelcomeDescription()

            // Login and Register Buttons
            LoginAndRegisterButtons(
                backStack = backStack,
                welcomeViewModel = welcomeViewModel
            )

            // Google Buttons
            GoogleButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        welcomeViewModel.updateAuthState(AuthState.NotAuthenticated)
                        welcomeViewModel.onGoogleSignIn(context)
                    }
                }
            )

            // Anonymous Button
            AnonymousButton(
                onClick = {
                    welcomeViewModel.updateAuthState(AuthState.NotAuthenticated)
                    welcomeViewModel.onAnonymousLogin()
                }
            )

            // Welcome footer
            Footer()
        }
    }
}

@Composable
fun AppTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_name).title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WelcomeDescription() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.welcome_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}


