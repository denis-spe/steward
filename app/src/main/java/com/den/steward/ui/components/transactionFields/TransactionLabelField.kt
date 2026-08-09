// Bless be the LORD GOD
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ListItem
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
    displayText: MutableState<String>,
    placeholder: String,
    textLength: Int = 16,
    wasSuccess: MutableState<TransactionFieldState>,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    colorResId: Int,
) {
    val isError = state.text.isEmpty() && wasSuccess.value is TransactionFieldState.Error
    val color = if (isError)
        MaterialTheme.colorScheme.error.copy(0.7f) else
        Color.Unspecified
    val modifiedPlaceholder = if (isError)
        "Fill the Label" else placeholder

    val onDialogShow = remember { mutableStateOf(false) }
    val optionsTitle = "Required"
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.text) {
        wasSuccess.value = TransactionFieldState.Initial
    }

    if (onDialogShow.value) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Dialog(
            onDismissRequest = {
                state.setTextAndPlaceCursorAtEnd(displayText.value)
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
                                displayText.value = state.text.toString()
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
                                state.setTextAndPlaceCursorAtEnd(displayText.value)
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
                                    displayText.value = state.text.toString()
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
        displayState = displayText
    )
}

@Composable
private fun TransactionLabelFieldItem(
    title: String,
    optionsTitle: String,
    modifier: Modifier = Modifier,
    onDialogShow: MutableState<Boolean>,
    displayState: MutableState<String>,
    color: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .height(LIST_ITEM_HEIGHT)
                .clickable {
                    onDialogShow.value = true
                },
            colors = ListItemDefaults.colors(
                containerColor = color
            ),
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.label),
                    contentDescription = "labelOrNote",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },

            headlineContent = {
                Text(title, fontSize = FONT_SIZE, fontWeight = FONT_WEIGHT)
            },

            trailingContent = {
                val textValue = if (displayState.value.length > MAX_LABEL_LENGTH)
                    displayState.value.take(MAX_LABEL_LENGTH) + "..." else
                    (displayState.value.ifEmpty { optionsTitle })

                Text(textValue, fontSize = FONT_SIZE)
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(bottom = 2.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
