package com.den.steward.ui.screens.authScreen.registerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.viewModels.RegisterViewModel
import com.den.steward.helper.isEmailValid
import com.den.steward.helper.pop
import com.den.steward.ui.screens.authScreen.authComponent.AuthButton
import com.den.steward.ui.screens.authScreen.authComponent.EmailAuthField
import com.den.steward.ui.screens.components.BackButton
import com.den.steward.ui.screens.components.Footer
import com.den.steward.ui.screens.screenManager.PasswordRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailScreen(
    backStack: NavBackStack<NavKey>,
    registerViewModel: RegisterViewModel
) {
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
        EmailScreenContent(
            padding = padding,
            backStack = backStack,
            registerViewModel = registerViewModel
        )
    }
}

@Composable
private fun EmailScreenContent(
    padding: PaddingValues,
    backStack: NavBackStack<NavKey>,
    registerViewModel: RegisterViewModel
) {
    val onScrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(onScrollState)
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Email Title
            EmailTitle()

            // Email Description
            EmailDescription()

            // Email Form
            EmailForm(
                registerViewModel = registerViewModel,
                backStack = backStack
            )

            // Email Footer
            Footer()
        }
    }
}

@Composable
private fun EmailTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.email_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmailDescription() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.email_screen_description),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmailForm(backStack: NavBackStack<NavKey>, registerViewModel: RegisterViewModel) {
    val emailState = rememberTextFieldState()
    val errorMessage = remember { mutableStateOf("") }

    val isEmailValid by remember {
        derivedStateOf { emailState.text.toString().isEmailValid }
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(emailState.text) {
        errorMessage.value = ""
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    EmailAuthField(
        modifier = Modifier.focusRequester(focusRequester)
            .testTag(stringResource(R.string.email_screen_email_field)),
        textState = emailState,
        supportingText = errorMessage.value,
        onNextClick = {
            if (isEmailValid == null) {
                registerViewModel.updateEmail(emailState.text.toString())
                backStack.add(PasswordRouter)
            } else {
                errorMessage.value = isEmailValid ?: ""
            }
        }
    )

    AuthButton(
        modifier = Modifier.testTag(stringResource(R.string.email_screen_next_button)),
        text = stringResource(R.string.email_screen_next_button),
        isError = errorMessage.value.isNotEmpty(),
    ) {
        if (isEmailValid == null) {
            registerViewModel.updateEmail(emailState.text.toString())
            backStack.add(PasswordRouter)
        } else {
            errorMessage.value = isEmailValid ?: ""
        }
    }
}