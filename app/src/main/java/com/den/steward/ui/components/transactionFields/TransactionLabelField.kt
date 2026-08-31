// Bless be the LORD GOD
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.R
import com.den.steward.helper.setTextAndPlaceCursorAtEnd
import kotlin.text.ifEmpty

@Composable
fun TransactionLabelField(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
    state: TextFieldState,
    displayText: String,
    onDisplayTextChange: (String) -> Unit = {},
    placeholder: String,
    textLength: Int = 16,
    wasSuccess: TransactionFieldState,
    updateWasSuccess: (TransactionFieldState) -> Unit = {},
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    colorResId: Int,
) {
    val isError = state.text.isEmpty() && wasSuccess is TransactionFieldState.Error
    val color = if (isError)
        MaterialTheme.colorScheme.error.copy(0.7f) else
        Color.Unspecified
    val modifiedPlaceholder = if (isError)
        "Fill the Label" else placeholder

    val onDialogShow = remember { mutableStateOf(false) }
    val optionsTitle = if (isError) "Required" else "..."
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.text) {
        updateWasSuccess(TransactionFieldState.Initial)
    }

    if (onDialogShow.value) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Dialog(
            onDismissRequest = {
                state.setTextAndPlaceCursorAtEnd(displayText)
                onDialogShow.value = false
            },
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.tag
                            ),
                            contentDescription = "Label",
                            modifier = Modifier.size(ICON_SIZE)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(title, fontSize = FONT_SIZE, fontWeight = FontWeight.Bold)
                    }
                    Text(description, textAlign = TextAlign.Center)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .padding(
                                vertical = 5.dp,
                                horizontal = 5.dp
                            ),
                        state = state,
                        lineLimits = lineLimits,
                        placeholder = {
                            Text(
                                text = modifiedPlaceholder,
                                fontSize = FONT_SIZE
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            unfocusedTextColor = color.copy(alpha = 0.5f),
                            cursorColor = color,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                        textStyle = TextStyle(
                            fontSize = FONT_SIZE
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        onKeyboardAction = KeyboardActionHandler {
                            if (state.text.isNotEmpty()) {
                                onDialogShow.value = false
                                onDisplayTextChange(state.text.toString())
                            }
                        },
                        trailingIcon = {
                            if (state.text.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        state.setTextAndPlaceCursorAtEnd("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,

                                        contentDescription = "clear text"
                                    )
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        inputTransformation = InputTransformation.maxLength(textLength)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp)
                    ) {
                        TextButton(
                            onClick = {
                                state.setTextAndPlaceCursorAtEnd(displayText)
                                onDialogShow.value = false
                            }
                        ) {
                            Text(
                                "Cancel", fontSize = FONT_SIZE,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Text(
                            "|",
                            modifier = Modifier.padding(horizontal = 2.dp),
                            color = colorResource(colorResId)
                        )

                        TextButton(
                            onClick = {
                                if (state.text.isNotEmpty()) {
                                    onDialogShow.value = false
                                    onDisplayTextChange(state.text.toString())
                                }
                            }
                        ) {
                            Text(
                                "OK",
                                fontSize = FONT_SIZE,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

            }
        }
    }

    TransactionLabelFieldItem(
        title = "Label",
        optionsTitle = optionsTitle,
        modifier = modifier,
        onDialogShow = onDialogShow,
        color = color,
        displayState = displayText,
        wasSuccess = wasSuccess
    )
}

@Composable
private fun TransactionLabelFieldItem(
    title: String,
    optionsTitle: String,
    modifier: Modifier = Modifier,
    onDialogShow: MutableState<Boolean>,
    displayState: String,
    color: Color,
    wasSuccess: TransactionFieldState,
) {
    val textColor = if (wasSuccess is TransactionFieldState.Error)
        Color.Red else Color.Unspecified

    TransactionFieldCard(
        title = title,
        modifier = modifier,
        leadingContent = {
            Image(
                painter = painterResource(R.drawable.label),
                contentDescription = "labelOrNote",
                modifier = Modifier.size(ICON_SIZE)
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = color
        ),
        trailingContent = {
            val textValue = if (displayState.length > MAX_LABEL_LENGTH)
                displayState.take(MAX_LABEL_LENGTH) + "..." else
                (displayState.ifEmpty { optionsTitle })

            Text(textValue, fontSize = FONT_SIZE, color = textColor)
        }
    ) {
        onDialogShow.value = true
    }
}
