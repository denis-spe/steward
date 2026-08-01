package com.den.steward.ui.screens.homeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.viewModels.HomeViewModel

@Composable
fun HomeScreen(backStack: NavBackStack<NavKey>, homeViewModel: HomeViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
    }
}