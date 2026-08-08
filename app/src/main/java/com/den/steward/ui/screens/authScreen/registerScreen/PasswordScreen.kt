package com.den.steward.ui.screens.authScreen.registerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.RegisterViewModel
import com.den.steward.helper.isPasswordValid
import com.den.steward.helper.pop
import com.den.steward.ui.screens.authScreen.authComponent.AuthButton
import com.den.steward.ui.screens.authScreen.authComponent.PasswordAuthField
import com.den.steward.ui.screens.componentExtenison.BoxNotification
import com.den.steward.ui.components.BackButton
import com.den.steward.ui.components.Footer
import com.den.steward.ui.screens.screenManager.HomeRouter
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    backStack: NavBackStack<NavKey>,
    registerViewModel: RegisterViewModel,
) {
    val passwordState = rememberTextFieldState()
    val confirmPasswordState = rememberTextFieldState()
    val passwordMessage = remember { mutableStateOf("") }
    val confirmPasswordMessage = remember { mutableStateOf("") }

    val isPasswordValidStr by remember {
        derivedStateOf { passwordState.text.toString().isPasswordValid }
    }
    val isConfirmPasswordValidStr by remember {
        derivedStateOf {
            if (confirmPasswordState.text.toString() != passwordState.text.toString()) {
                "Passwords do not match"
            } else {
                null
            }
        }
    }

    LaunchedEffect(passwordState.text, confirmPasswordState.text) {
        passwordMessage.value = ""
        confirmPasswordMessage.value = ""
    }

    // User state management
    val userState by registerViewModel.userState.collectAsStateWithLifecycle()

    // Loading state
    val isLoading by registerViewModel.isLoading.collectAsStateWithLifecycle()

    // Navigation logic handled in LaunchedEffect to avoid side effects during composition
    // Navigation logic handled in LaunchedEffect to avoid side effects during composition
    LaunchedEffect(userState) {
        if (userState is AuthState.Authenticated) {
            backStack.clear()
            backStack.add(HomeRouter)
        } else if (userState is AuthState.Error) {
            delay(3000.milliseconds) // Delay for 3 seconds
            registerViewModel.updateAuthState(AuthState.NotAuthenticated)
        }
    }

    val serverError = if (userState is AuthState.Error) {
        (userState as AuthState.Error).message
    } else {
        null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
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
                visible = serverError != null,
                notificationText = serverError
            )

            PasswordContent(
                passwordState = passwordState,
                confirmPasswordState = confirmPasswordState,
                passwordError = passwordMessage.value,
                confirmPasswordError = confirmPasswordMessage.value
            ) {
                if ((isPasswordValidStr == null) && (isConfirmPasswordValidStr == null)) {
                    registerViewModel.updatePassword(passwordState.text.toString())
                    registerViewModel.registerUser()
                } else {
                    passwordMessage.value = isPasswordValidStr ?: ""
                    confirmPasswordMessage.value = isConfirmPasswordValidStr ?: ""
                }
            }

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
private fun PasswordContent(
    passwordState: TextFieldState,
    confirmPasswordState: TextFieldState,
    passwordError: String,
    confirmPasswordError: String,
    onRegisterClick: () -> Unit
) {
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        passwordFocusRequester.requestFocus()
        keyboardController?.show()
    }

    val onScrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(onScrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PasswordTitle()
            PasswordDescription()

            PasswordAuthField(
                modifier = Modifier.focusRequester(passwordFocusRequester)
                    .testTag(stringResource(R.string.password_screen_password_field)),
                textState = passwordState,
                supportingText = passwordError,
                imeAction = ImeAction.Next
            ) {
                confirmPasswordFocusRequester.requestFocus()
            }

            PasswordAuthField(
                label = "Confirm Password",
                modifier = Modifier.focusRequester(confirmPasswordFocusRequester)
                    .testTag(stringResource(R.string.password_screen_confirm_password_field)),
                textState = confirmPasswordState,
                supportingText = confirmPasswordError,
                imeAction = ImeAction.Send
            ) {
                onRegisterClick()
            }

            AuthButton(
                modifier = Modifier.testTag(stringResource(R.string.password_screen_register_button)),
                onClick = onRegisterClick,
                text = stringResource(R.string.password_screen_register_button),
                isError = passwordError.isNotEmpty() || confirmPasswordError.isNotEmpty()
            )

            Footer()
        }
    }
}

@Composable
private fun PasswordTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.password_screen_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PasswordDescription() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.password_screen_description),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
