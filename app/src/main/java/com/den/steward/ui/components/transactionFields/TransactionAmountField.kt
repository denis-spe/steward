// Grace and truth came through JESUS
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.den.steward.R
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.getCurrencySymbol
import com.den.steward.helper.setTextAndPlaceCursorAtEnd
import com.den.steward.ui.components.CustomAmountKeyBoard


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TransactionAmountField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String,
    shape: Shape = CircleShape,
    wasSuccess: MutableState<TransactionFieldState>,
    displayState: MutableState<String>,
    clearOnCancel: Boolean = false,
) {
    val isError = wasSuccess.value is TransactionFieldState.Error
    val color = if (isError)
        MaterialTheme.colorScheme.error.copy(0.7f) else
        Color.Unspecified

    val symbol = getCurrencySymbol()
    val onDialogShow = remember { mutableStateOf(false) }
    val showCustomKeyboard = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
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
                onDialogShow.value = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = DIALOG_CARD_MODIFIER
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.amount),
                                contentDescription = "Amount"
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Amount", fontSize = FONT_SIZE, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Text("Enter the amount", textAlign = TextAlign.Center)
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    }

                    item {
                        OutlinedTextField(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        keyboardController?.hide()
                                        showCustomKeyboard.value = true
                                    }
                                },
                            state = state,
                            shape = shape,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            placeholder = {
                                Text(
                                    text = placeholder,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AMOUNT_FONT_SIZE
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors().copy(
                                unfocusedTextColor = color.copy(alpha = 0.5f),
                                cursorColor = color,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = AMOUNT_FONT_SIZE
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                showKeyboardOnFocus = false,
                                imeAction = ImeAction.Done
                            ),
                            onKeyboardAction = KeyboardActionHandler {
                                if (state.text.isNotEmpty()) {
                                    onDialogShow.value = false
                                    displayState.value = if (state.text.isNotEmpty())
                                        state.text.toString() else
                                        "0.0"
                                }
                            },
                            leadingIcon = {
                                Text(
                                    text = symbol,
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AMOUNT_FONT_SIZE
                                )
                            },
                            inputTransformation = CustomInputTransformation(),
                            outputTransformation = CustomOutputTransformation(),
                        )
                    }

                    item {
                        CustomAmountKeyBoard(
                            state = state,
                            focusRequester = focusRequester,
                            visible = showCustomKeyboard.value,
                            contentColor = color,
                            onDone = {
                                if (state.text.isNotEmpty()) {
                                    state.setTextAndPlaceCursorAtEnd(state.text.toString())
                                    displayState.value = state.text.toString()
                                    onDialogShow.value = false
                                    showCustomKeyboard.value = false
                                }
                            },
                            onCancel = {
                                onDialogShow.value = false
                                if (clearOnCancel)
                                    state.setTextAndPlaceCursorAtEnd("")
                                else
                                    state.setTextAndPlaceCursorAtEnd(displayState.value)
                            }
                        )
                    }
                }
            }
        }
    }


    TransactionAmountFieldItem(
        modifier = modifier,
        showCustomKeyboard = showCustomKeyboard,
        onDialogShow = onDialogShow,
        displayState = displayState,
        color = color,
        symbol = symbol
    )

}


@Composable
private fun TransactionAmountFieldItem(
    modifier: Modifier = Modifier,
    showCustomKeyboard: MutableState<Boolean>,
    onDialogShow: MutableState<Boolean>,
    displayState: MutableState<String>,
    symbol: String,
    color: Color
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
                    showCustomKeyboard.value = true
                    onDialogShow.value = true
                },
            headlineContent = {
                Text("Amount", fontSize = FONT_SIZE, fontWeight = FONT_WEIGHT)
            },
            colors = ListItemDefaults.colors(
                containerColor = color,
            ),
            leadingContent = {
                Image(
                    painter = painterResource(id = R.drawable.amount),
                    contentDescription = "Amount",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },
            trailingContent = {
                val amountText = try {
                    if (displayState.value.isEmpty()) "$symbol 0.0" else
                        displayState.value.toDouble().formatToAmount()
                } catch (e: Exception) {
                    "$symbol 0.0"
                }
                Text(amountText, fontSize = FONT_SIZE)
            }

        )
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 2.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}