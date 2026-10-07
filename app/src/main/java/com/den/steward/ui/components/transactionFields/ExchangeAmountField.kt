package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.den.steward.helper.getCurrencySymbol

@Composable
fun ExchangeAmountField(
    state: TextFieldState,
) {
    val symbol = getCurrencySymbol()

    Surface(
        modifier = Modifier.fillMaxWidth(0.8f),
        shape = CircleShape,
        shadowElevation = 2.dp
    ) {
        TextField(
            state = state,
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    state.edit {
                        selection = TextRange(length)
                    }
                }
            },
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = AMOUNT_FONT_SIZE
            ),
            placeholder = {
                Text(
                    text = "0.0",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        ),
                        fontSize = AMOUNT_FONT_SIZE
                    )
                )
            },

            leadingIcon = {
                Text(
                    text = symbol,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = AMOUNT_FONT_SIZE
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            inputTransformation = CustomInputTransformation(),
            outputTransformation = CustomOutputTransformation(),
        )
    }
}