// Love the LORD your GOD with all your soul and with all your heart
// and with all your might and love your neighbor as yourself
package com.den.steward.ui.screens.authScreen.authComponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.den.steward.ui.theme.StewardTheme

@Composable
fun EmailAuthField(
    textState: TextFieldState,
    supportingText: String?,
) {
    TextField(
        state = textState,
        label = {
            Text(
                text = "Email Address",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingText = {
            if (!supportingText.isNullOrEmpty()) {
                Text(
                    text = supportingText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        isError = !supportingText.isNullOrEmpty(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            showKeyboardOnFocus = true,
            imeAction = ImeAction.Next
        )
    )
}

@Composable
fun PasswordAuthField(
    textState: TextFieldState,
    supportingText: String?,
    imeAction: ImeAction? = null,
    onNextClick: () -> Unit,
) {
    val showPassword = remember { mutableStateOf(false) }

    SecureTextField(
        state = textState,
        label = {
            Text(
                text = "Password",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingText = {
            if (!supportingText.isNullOrEmpty()) {
                Text(
                    text = supportingText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        isError = !supportingText.isNullOrEmpty(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            showKeyboardOnFocus = true,
            imeAction = imeAction ?: ImeAction.Send
        ),

        onKeyboardAction = KeyboardActionHandler {
            onNextClick()
        },
        inputTransformation = InputTransformation.maxLength(8),
        textObfuscationMode = if (showPassword.value) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        },
        trailingIcon = {
            IconToggleButton(
                checked = showPassword.value,
                onCheckedChange = { showPassword.value = it }
            ) {
                Icon(
                    imageVector = if (showPassword.value) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = if (showPassword.value) {
                        "Hide password"
                    } else {
                        "Show password"
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun AuthFieldPreview() {
    val textState = rememberTextFieldState()
    StewardTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EmailAuthField(
                textState = textState,
                supportingText = null
            )

            PasswordAuthField(
                textState = textState,
                supportingText = null,
                onNextClick = {}
            )
        }
    }
}


