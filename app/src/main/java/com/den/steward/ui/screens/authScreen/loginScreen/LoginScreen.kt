package com.den.steward.ui.screens.authScreen.loginScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.backend.viewModels.LoginViewModel
import com.den.steward.ui.screens.screenManager.HomeRouter

@Composable
fun LoginScreen(
    backStack: NavBackStack<NavKey>,
) {
    // Text fields states
    val emailState = rememberTextFieldState()
    val emailMessage = remember { mutableStateOf("") }
    val passwordState = rememberTextFieldState()
    val passwordMessage = remember { mutableStateOf("") }

    // Initialize the ViewModel
    val loginViewModel = hiltViewModel<LoginViewModel>()


    LaunchedEffect(emailState.text, passwordState.text) {
        emailMessage.value = ""
        passwordMessage.value = ""
    }

    // User state management
    val userState by loginViewModel.userState.collectAsStateWithLifecycle()

    // Loading state
    val isLoading by loginViewModel.isLoading.collectAsStateWithLifecycle()

    // Navigation logic handled in LaunchedEffect to avoid side effects during composition
    LaunchedEffect(userState) {
        if (userState is AuthState.Authenticated) {
            backStack.clear()
            backStack.add(HomeRouter)
        }
    }

    // Server error message
    val serverErrorMessage = if (userState is AuthState.Error) {
        (userState as AuthState.Error).message
    } else {
        null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
    }
}