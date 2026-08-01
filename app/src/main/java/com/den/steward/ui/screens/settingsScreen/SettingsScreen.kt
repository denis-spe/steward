package com.den.steward.ui.screens.settingsScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.viewModels.SettingsViewModel

@Composable
fun SettingsScreen(backStack: NavBackStack<NavKey>, settingsViewModel: SettingsViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
    }
}