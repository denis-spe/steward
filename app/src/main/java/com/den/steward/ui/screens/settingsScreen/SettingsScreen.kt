package com.den.steward.ui.screens.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.SettingsViewModel
import com.den.steward.ui.screens.screenManager.WelcomeRouter

@Composable
fun SettingsScreen(backStack: NavBackStack<NavKey>, settingsViewModel: SettingsViewModel) {
    val userState by settingsViewModel.userState.collectAsStateWithLifecycle()

    if (userState is AuthState.NotAuthenticated) {
        backStack.clear()
        backStack.add(WelcomeRouter)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.testTag(stringResource(R.string.settings_screen_logout_button)),
                onClick = {
                    settingsViewModel.logout()
                }
            ) {
                Text(stringResource(R.string.settings_screen_logout_button))
            }
        }
    }
}