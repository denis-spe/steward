// Grace and truth came through JESUS
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
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
import com.den.steward.backend.entitles.Transaction
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
    isAmountCorrect: TransactionFieldState,
    updateIsAmountCorrect: (TransactionFieldState) -> Unit = {},
    displayState: String,
    updateDisplayState: (String) -> Unit = {},
    clearOnCancel: Boolean = false,
    transactionName: String? = null,
    transactions: List<Transaction>? = null,
    onSetItem: (transaction: Transaction) -> Unit = {},
    colorResId: Int,
) {
    val isError = isAmountCorrect is TransactionFieldState.Error
    val color = if (isError)
        MaterialTheme.colorScheme.error else
        colorResource(id = colorResId)

    val symbol = getCurrencySymbol()
    val onDialogShow = remember { mutableStateOf(false) }
    val showCustomKeyboard = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val selectedTransaction = remember { mutableStateOf<Transaction?>(null) }
    val placeholderText = remember { mutableStateOf(placeholder) }

    LaunchedEffect(state.text, selectedTransaction.value) {
        updateIsAmountCorrect(TransactionFieldState.Initial)

        if (selectedTransaction.value != null) {
            val amount = when (val tx = selectedTransaction.value) {
                is Transaction.Lent -> tx.remainingAmount
                is Transaction.Debt -> tx.remainingAmount
                is Transaction.Goal -> tx.remainingValue
                else -> 0.0
            }
            placeholderText.value = amount.formatToAmount().replace(symbol, "").trim()
        } else {
            placeholderText.value = placeholder
        }
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

                        if (transactions == null) return@item

                        TransactionAmountFieldFulfilment(
                            state = state,
                            transactionName = transactionName,
                            transaction = transactions,
                            selectedTransaction = selectedTransaction,
                            onSetItem = onSetItem,
                        )
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
                                    text = placeholderText.value,
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
                                    updateDisplayState(
                                        if (state.text.isNotEmpty())
                                            state.text.toString() else
                                            "0.0"
                                    )
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
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            onDone = {
                                if (state.text.isNotEmpty()) {
                                    state.setTextAndPlaceCursorAtEnd(state.text.toString())
                                    updateDisplayState(state.text.toString())
                                    onDialogShow.value = false
                                    showCustomKeyboard.value = false
                                }
                            },
                            onCancel = {
                                onDialogShow.value = false
                                if (clearOnCancel)
                                    state.setTextAndPlaceCursorAtEnd("")
                                else
                                    state.setTextAndPlaceCursorAtEnd(displayState)
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
        symbol = symbol,
        isError = isError,
        isAmountCorrect = isAmountCorrect,
        color = color
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAmountFieldFulfilment(
    state: TextFieldState,
    transactionName: String?,
    transaction: List<Transaction>,
    selectedTransaction: MutableState<Transaction?>,
    onSetItem: (transaction: Transaction) -> Unit,
) {
    val expand = remember { mutableStateOf(false) }
    if (transactionName == null) return

    val fulfilmentName = when (transactionName) {
        "Lent" -> "repayment"
        "Debt" -> "settlement"
        else -> "attainment"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedCard(
            onClick = { expand.value = true },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (selectedTransaction.value != null)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                else Color.Transparent
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (selectedTransaction.value != null)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = selectedTransaction.value?.type?.icon ?: R.drawable.ic_attain
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (selectedTransaction.value != null)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedTransaction.value?.let { "Fulfill: ${it.getLabel}" }
                        ?: "Select a $transactionName to add $fulfilmentName",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedTransaction.value != null)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (expand.value) {
            FulfillmentList(
                expand = expand,
                transactionName = transactionName,
                transaction = transaction,
                state = state,
                selectedTransaction = selectedTransaction,
                onSetItem = onSetItem
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FulfillmentList(
    expand: MutableState<Boolean>,
    transactionName: String,
    transaction: List<Transaction>,
    state: TextFieldState,
    selectedTransaction: MutableState<Transaction?>,
    onSetItem: (transaction: Transaction) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            expand.value = false
        },
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select ${transactionName.lowercase()} to fulfill",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            if (selectedTransaction.value != null) {
                TextButton(
                    onClick = {
                        state.setTextAndPlaceCursorAtEnd("")
                        selectedTransaction.value = null
                        expand.value = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Clear Selection", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (transaction.isNotEmpty()) {
                    items(transaction.size) { index ->
                        val item = transaction[index]
                        val isSelected = selectedTransaction.value?.id == item.id

                        OutlinedCard(
                            onClick = {
                                state.setTextAndPlaceCursorAtEnd("")
                                selectedTransaction.value = item
                                onSetItem(item)
                                expand.value = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = if (isSelected)
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            else
                                CardDefaults.outlinedCardBorder()
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        item.getLabel,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                supportingContent = {
                                    val remaining = when (item) {
                                        is Transaction.Lent -> item.remainingAmount
                                        is Transaction.Debt -> item.remainingAmount
                                        is Transaction.Goal -> item.remainingValue
                                        else -> 0.0
                                    }
                                    Text("Remaining: ${remaining.formatToAmount()}")
                                },
                                leadingContent = {
                                    item.getIcon?.let {
                                        Icon(
                                            painter = painterResource(id = it),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Unspecified
                                        )
                                    }
                                },
                                trailingContent = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close, // Reusing close as a marker or check
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else
                                        Color.Transparent
                                )
                            )
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_empty_transactions),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                                Text(
                                    "No ${transactionName.lowercase()} found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}


@Composable
private fun TransactionAmountFieldItem(
    modifier: Modifier = Modifier,
    showCustomKeyboard: MutableState<Boolean>,
    onDialogShow: MutableState<Boolean>,
    displayState: String,
    symbol: String,
    color: Color,
    isError: Boolean,
    isAmountCorrect: TransactionFieldState = TransactionFieldState.Initial
) {

    TransactionFieldCard(
        title = "Amount",
        modifier = modifier,
        headlineContent = {
            if (isAmountCorrect is TransactionFieldState.Error) {
                Text(
                    text = isAmountCorrect.message,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        },
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.amount),
                contentDescription = "Amount",
                modifier = Modifier.size(ICON_SIZE)
            )
        },
        colors = ListItemDefaults.colors(
            headlineColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            supportingColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        trailingContent = {
            val amountText = try {
                if (displayState.isEmpty()) "$symbol 0.0" else
                    displayState.toDouble().formatToAmount()
            } catch (_: Exception) {
                "$symbol 0.0"
            }
            Text(
                text = if (isError) "Required" else amountText,
                fontWeight = FontWeight.Bold,
                color = color,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = FONT_SIZE
                )
            )
        }
    ) {
        showCustomKeyboard.value = true
        onDialogShow.value = true
    }
}