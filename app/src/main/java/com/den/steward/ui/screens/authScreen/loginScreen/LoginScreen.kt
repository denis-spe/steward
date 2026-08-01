package com.den.steward.ui.screens.authScreen.loginScreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.LoginViewModel
import com.den.steward.helper.isEmailValid
import com.den.steward.helper.isPasswordValid
import com.den.steward.ui.screens.authScreen.authComponent.AuthButton
import com.den.steward.ui.screens.authScreen.authComponent.EmailAuthField
import com.den.steward.ui.screens.authScreen.authComponent.PasswordAuthField
import com.den.steward.ui.screens.screenManager.HomeRouter
import com.den.steward.ui.screens.welcomeScreen.ShowServerMessage
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoginScreen(
    backStack: NavBackStack<NavKey>,
) {
    // Text fields states
    val emailState = rememberTextFieldState()
    val emailMessage = remember { mutableStateOf("") }
    val passwordState = rememberTextFieldState()
    val passwordMessage = remember { mutableStateOf("") }
    val isEmailValid = emailState.text.toString().isEmailValid
    val isPasswordValid = passwordState.text.toString().isPasswordValid

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
        } else if (userState is AuthState.Error) {
            delay(3000.milliseconds) // Delay for 3 seconds
            loginViewModel.updateAuthState(AuthState.NotAuthenticated)
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
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(padding),
        ) {
            AnimatedVisibility(
                visible = userState is AuthState.Error,
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

            // Login contents
            LoginContent(
                emailState = emailState,
                passwordState = passwordState,
                isEmailValid = emailMessage,
                isPasswordValid = passwordMessage
            ) {
                Log.d("LoginScreen", passwordState.text.toString())
                if (isEmailValid == null && isPasswordValid == null) {
                    loginViewModel.login(
                        email = emailState.text.toString(),
                        password = passwordState.text.toString()
                    )
                } else {
                    emailMessage.value = isEmailValid ?: ""
                    passwordMessage.value = isPasswordValid ?: ""
                }
            }

            if (isLoading) {
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

@Composable
fun LoginContent(
    emailState: TextFieldState,
    passwordState: TextFieldState,
    isEmailValid: MutableState<String>,
    isPasswordValid: MutableState<String>,
    onLoginClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Login title
            LoginTitle()

            // Login description
            LoginDescription()

            // Email and password fields
            EmailAuthField(
                textState = emailState,
                supportingText = isEmailValid.value
            )

            PasswordAuthField(
                textState = passwordState,
                supportingText = isPasswordValid.value,
                onNextClick = onLoginClick
            )

            // Login button
            AuthButton(
                onClick = onLoginClick,
                text = "Login",
                isError = isEmailValid.value.isNotEmpty() || isPasswordValid.value.isNotEmpty()
            )

            // Login footer
            LoginFooter()
        }
    }
}

@Composable
fun LoginTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoginDescription() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Login to your steward account",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun LoginFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(40.dp)
        )
        Text(
            text = "Copy right©2023, All rights reserved",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = "Glory be to the name of LORD of host",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
