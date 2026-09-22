// Bless be the LORD of host and our LORD JESUS CHRIST
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PlanFulfillmentAmountField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String,
    isError: Boolean = false,
) {
    OutlinedTextField(
        state = state,
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        isError = isError,
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
        colors = TextFieldDefaults.colors()
            .copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
        shape = MaterialTheme.shapes.medium
    )
}