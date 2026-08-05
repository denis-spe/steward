// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.authScreen.forgotPasswordScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.ForgotPasswordViewModel
import com.den.steward.helper.isEmailValid
import com.den.steward.helper.pop
import com.den.steward.ui.screens.authScreen.authComponent.AuthButton
import com.den.steward.ui.screens.authScreen.authComponent.EmailAuthField
import com.den.steward.ui.screens.componentExtenison.BoxNotification
import com.den.steward.ui.screens.components.BackButton
import com.den.steward.ui.screens.components.Footer
import com.den.steward.ui.screens.screenManager.HomeRouter
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    backStack: NavBackStack<NavKey>,
    forgotPasswordViewModel: ForgotPasswordViewModel
) {
    // Text fields states
    val emailState = rememberTextFieldState()
    val emailMessage = remember { mutableStateOf("") }

    val isEmailValid by remember {
        derivedStateOf { emailState.text.toString().isEmailValid }
    }

    LaunchedEffect(emailState.text) {
        emailMessage.value = ""
    }


    // User state management
    val userState by forgotPasswordViewModel.userState.collectAsStateWithLifecycle()

    // Loading state
    val isLoading by forgotPasswordViewModel.isLoading.collectAsStateWithLifecycle()

    // Navigation logic handled in LaunchedEffect to avoid side effects during composition
    LaunchedEffect(userState) {
        if (userState is AuthState.Authenticated) {
            backStack.clear()
            backStack.add(HomeRouter)
        } else if (userState is AuthState.Error || userState is AuthState.Success) {
            delay(3000.milliseconds) // Delay for 3 seconds
            forgotPasswordViewModel.updateAuthState(AuthState.NotAuthenticated)
        }
    }

    // Server message
    val serverMessage = when (userState) {
        is AuthState.Error -> (userState as AuthState.Error).message
        is AuthState.Success -> (userState as AuthState.Success).message
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton {
                        backStack.pop()
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(padding),
        ) {
            BoxNotification(
                visible = serverMessage != null,
                notificationText = serverMessage,
                isSuccessMessage = userState is AuthState.Success
            )

            ForgotPasswordContent(
                emailState = emailState,
                emailError = emailMessage.value,
                onResetClick = {
                    if (isEmailValid == null) {
                        forgotPasswordViewModel.forgotPassword(emailState.text.toString())
                    } else {
                        emailMessage.value = isEmailValid ?: ""
                    }
                }
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .testTag(stringResource(R.string.loading_indicator)),
                    trackColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordContent(
    emailState: TextFieldState,
    emailError: String,
    onResetClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ForgotPasswordTitle()
            ForgotPasswordDescription()

            EmailAuthField(
                modifier = Modifier.focusRequester(focusRequester)
                    .testTag(stringResource(R.string.forgot_password_email_field)),
                textState = emailState,
                supportingText = emailError,
                onNextClick = onResetClick
            )

            AuthButton(
                modifier = Modifier.testTag(stringResource(R.string.forgot_password_send_button)),
                onClick = onResetClick,
                text = stringResource(R.string.forgot_password_send_button),
                isError = emailError.isNotEmpty()
            )

            Footer()
        }
    }
}

@Composable
private fun ForgotPasswordTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.forgot_password_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ForgotPasswordDescription() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.forgot_password_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
