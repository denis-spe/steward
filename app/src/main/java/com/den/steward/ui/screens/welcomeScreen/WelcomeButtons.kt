// Glory be to the LORD GOD of hosts
package com.den.steward.ui.screens.welcomeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.ui.screens.screenManager.LoginRouter
import com.den.steward.ui.screens.screenManager.NameRouter
import com.den.steward.ui.theme.ExtendedTheme
import com.den.steward.ui.theme.StewardTheme

const val CORNER_RATE = 30f

@Composable
fun LoginAndRegisterButtons(backStack: NavBackStack<NavKey>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoginButton(
            onClick = {
                backStack.add(LoginRouter)
            }
        )
        RegisterButton(
            onClick = {
                backStack.add(NameRouter)
            }
        )
    }
}


@Composable
fun LoginButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(
            topStart = CORNER_RATE,
            bottomStart = CORNER_RATE,
        )
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RegisterButton(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(
            topEnd = CORNER_RATE,
            bottomEnd = CORNER_RATE
        ),
        border = BorderStroke(2.dp, ExtendedTheme.colors.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ExtendedTheme.colors.primary
        )
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnonymousButton(
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
    ) {
        Text(
            text = "Anonymous",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun GoogleButton(
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
    ) {
        Text(
            text = buildAnnotatedString {
                append("Sign in with ")
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF4285F4)
                    )
                ) {
                    append("G")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFEA4335)
                    )
                ) {
                    append("o")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFFFC107)
                    )
                ) {
                    append("o")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF0F9D58)
                    )
                ) {
                    append("g")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF4285F4)
                    )
                ) {
                    append("l")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFEA4335)
                    )
                ) {
                    append("e")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}