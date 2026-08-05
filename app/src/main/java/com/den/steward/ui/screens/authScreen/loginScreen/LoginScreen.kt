package com.den.steward.ui.screens.authScreen.loginScreen

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.den.steward.helper.pop
import com.den.steward.ui.screens.authScreen.authComponent.AuthButton
import com.den.steward.ui.screens.authScreen.authComponent.AuthForgotPassword
import com.den.steward.ui.screens.authScreen.authComponent.EmailAuthField
import com.den.steward.ui.screens.components.Footer
import com.den.steward.ui.screens.authScreen.authComponent.PasswordAuthField
import com.den.steward.ui.screens.componentExtenison.BoxNotification
import com.den.steward.ui.screens.components.BackButton
import com.den.steward.ui.screens.screenManager.ForgotPasswordRouter
import com.den.steward.ui.screens.screenManager.HomeRouter
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    backStack: NavBackStack<NavKey>,
    loginViewModel: LoginViewModel,
) {
    // Text fields states
    val emailState = rememberTextFieldState()
    val emailMessage = remember { mutableStateOf("") }
    val passwordState = rememberTextFieldState()
    val passwordMessage = remember { mutableStateOf("") }

    val isEmailValid by remember {
        derivedStateOf { emailState.text.toString().isEmailValid }
    }
    val isPasswordValid by remember {
        derivedStateOf { passwordState.text.toString().isPasswordValid }
    }


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
                visible = serverErrorMessage != null,
                notificationText = serverErrorMessage
            )

            // Login contents
            LoginContent(
                emailState = emailState,
                passwordState = passwordState,
                emailError = emailMessage.value,
                passwordError = passwordMessage.value,
                onPasswordForgotClick = {
                    backStack.add(ForgotPasswordRouter)
                },
            ) {
                if ((isEmailValid == null) && (isPasswordValid == null)) {
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
private fun LoginContent(
    emailState: TextFieldState,
    passwordState: TextFieldState,
    emailError: String,
    passwordError: String,
    onPasswordForgotClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) {

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Login title
            LoginTitle()

            // Login description
            LoginDescription()

            // Email and password fields
            EmailAuthField(
                modifier = Modifier
                    .focusRequester(emailFocusRequester)
                    .testTag(stringResource(R.string.login_email_field)),
                textState = emailState,
                supportingText = emailError
            ) {
                passwordFocusRequester.requestFocus()
                keyboardController?.show()
            }


            Column {
                PasswordAuthField(
                    modifier = Modifier
                        .focusRequester(passwordFocusRequester)
                        .testTag(stringResource(R.string.login_password_field)),
                    textState = passwordState,
                    supportingText = passwordError,
                    onNextClick = onLoginClick
                )

                AuthForgotPassword(
                    modifier = Modifier.testTag(stringResource(R.string.login_forgot_password)),
                    onClick = onPasswordForgotClick
                )
            }

            // Login button
            AuthButton(
                modifier = Modifier.testTag(stringResource(R.string.login_button)),
                onClick = onLoginClick,
                text = "Login",
                isError = emailError.isNotEmpty() || passwordError.isNotEmpty()
            )

            // Login footer
            Footer()
        }
    }
}

@Composable
private fun LoginTitle() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .testTag(stringResource(R.string.login_screen_title)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LoginDescription() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.login_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
