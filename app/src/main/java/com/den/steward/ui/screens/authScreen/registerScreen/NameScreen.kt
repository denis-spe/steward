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
import com.den.steward.helper.isNameValid
import com.den.steward.helper.pop
import com.den.steward.ui.screens.authScreen.authComponent.AuthButton
import com.den.steward.ui.components.Footer
import com.den.steward.ui.screens.authScreen.authComponent.NameAuthField
import com.den.steward.ui.components.BackButton
import com.den.steward.ui.screens.screenManager.EmailRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameScreen(
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
        NameContent(
            padding = padding,
            backStack = backStack,
            registerViewModel = registerViewModel
        )
    }
}

@Composable
fun NameContent(
    padding: PaddingValues,
    backStack: NavBackStack<NavKey>,
    registerViewModel: RegisterViewModel
) {

    val firstNameState = rememberTextFieldState()
    val lastNameState = rememberTextFieldState()
    val firstNameError = remember { mutableStateOf("") }
    val lastNameError = remember { mutableStateOf("") }

    val isFirstNameValid by remember {
        derivedStateOf { firstNameState.text.toString().isNameValid }
    }

    val firstNameFocusRequester = remember { FocusRequester() }
    val lastNameFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(firstNameState.text, lastNameState.text) {
        firstNameError.value = ""
    }

    LaunchedEffect(Unit) {
        firstNameFocusRequester.requestFocus()
        keyboardController?.show()
    }


    val onScrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(onScrollState)
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Name title
            NameTitle()

            // Name description
            NameDescription()

            // First name field
            NameAuthField(
                modifier = Modifier.focusRequester(firstNameFocusRequester)
                    .testTag(stringResource(R.string.name_screen_first_name_field)),
                textState = firstNameState,
                supportingText = firstNameError.value,
                label = "First Name",
                onNextClick = {
                    lastNameFocusRequester.requestFocus()
                }
            )

            // Last name field
            NameAuthField(
                modifier = Modifier.focusRequester(lastNameFocusRequester)
                    .testTag(stringResource(R.string.name_screen_last_name_field)),
                textState = lastNameState,
                supportingText = lastNameError.value,
                label = "Last Name (Optional)",
                onNextClick = {
                    if (isFirstNameValid == null) {
                        registerViewModel.updateUserName(
                            firstName = firstNameState.text.toString(),
                            lastName = lastNameState.text.toString()
                        )
                        backStack.add(EmailRouter)
                    } else {
                        firstNameError.value = isFirstNameValid ?: ""
                    }
                }
            )

            AuthButton(
                modifier = Modifier.testTag(stringResource(R.string.name_screen_next_button)),
                onClick = {
                    if (isFirstNameValid == null) {
                        registerViewModel.updateUserName(
                            firstName = firstNameState.text.toString(),
                            lastName = lastNameState.text.toString()
                        )
                        backStack.add(EmailRouter)
                    } else {
                        firstNameError.value = isFirstNameValid ?: ""
                    }
                },
                text = "Next",
                isError = firstNameError.value.isNotEmpty() || lastNameError.value.isNotEmpty()
            )

            // Name footer
            Footer()
        }
    }
}

@Composable
fun NameTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.name_screen_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NameDescription() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.name_screen_description),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}