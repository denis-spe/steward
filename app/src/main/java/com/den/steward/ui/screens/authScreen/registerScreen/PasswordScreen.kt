package com.den.steward.ui.screens.authScreen.registerScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.RegisterViewModel
import com.den.steward.ui.screens.screenManager.HomeRouter

@Composable
fun PasswordScreen(
    backStack: NavBackStack<NavKey>,
    registerViewModel: RegisterViewModel
) {
    val passwordState = rememberTextFieldState()
    val confirmPasswordState = rememberTextFieldState()
    val passwordMessage = remember { mutableStateOf("") }
    val confirmPasswordMessage = remember { mutableStateOf("") }


    LaunchedEffect(passwordState.text, confirmPasswordState.text) {
        passwordMessage.value = ""
        confirmPasswordMessage.value = ""
    }

    // User state management
    val userState by registerViewModel.userState.collectAsStateWithLifecycle()

    // Loading state
    val isLoading by registerViewModel.isLoading.collectAsStateWithLifecycle()

    // Navigation logic handled in LaunchedEffect to avoid side effects during composition
    LaunchedEffect(userState) {
        if (userState is AuthState.Authenticated) {
            backStack.clear()
            backStack.add(HomeRouter)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
    }
}