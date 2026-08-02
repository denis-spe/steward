package com.den.steward.ui.screens.authScreen.registerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.viewModels.RegisterViewModel
import com.den.steward.helper.isNameValid
import com.den.steward.ui.screens.authScreen.authComponent.AuthButton
import com.den.steward.ui.screens.authScreen.authComponent.NameAuthField
import com.den.steward.ui.screens.screenManager.EmailRouter
import com.den.steward.ui.screens.screenManager.NameRouter

@Composable
fun NameScreen(
    backStack: NavBackStack<NavKey>,
    registerViewModel: RegisterViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
    val errorMessage = remember { mutableStateOf("") }
    val isNameValid = firstNameState.text.toString().isNameValid

    LaunchedEffect(firstNameState.text, lastNameState.text) {
        errorMessage.value = ""
    }

    Column(
        modifier = Modifier.fillMaxSize()
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
                textState = firstNameState,
                supportingText = errorMessage.value,
                label = "First Name",
                onNextClick = {
                    errorMessage.value = isNameValid ?: ""

                    if (isNameValid == null) {
                        registerViewModel.updateUserName(
                            firstName = firstNameState.text.toString(),
                            lastName = lastNameState.text.toString()
                        )
                        backStack.add(NameRouter)
                    }
                }
            )

            // Last name field
            NameAuthField(
                textState = lastNameState,
                supportingText = null,
                label = "Last Name",
                onNextClick = {
                    errorMessage.value = isNameValid ?: ""

                    if (isNameValid == null) {
                        registerViewModel.updateUserName(
                            firstName = firstNameState.text.toString(),
                            lastName = lastNameState.text.toString()
                        )
                        backStack.add(EmailRouter)
                    }
                }
            )

            AuthButton(
                onClick = {
                    errorMessage.value = isNameValid ?: ""

                    if (isNameValid == null) {
                        registerViewModel.updateUserName(
                            firstName = firstNameState.text.toString(),
                            lastName = lastNameState.text.toString()
                        )
                        backStack.add(EmailRouter)
                    }
                },
                text = "Next",
                isError = false
            )

            // Name footer
            NameFooter()
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
            text = "User Names",
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
            text = "Enter your names",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun NameFooter() {
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
    }
}