// Grace and truth came through JESUS
package com.den.steward.ui.components.bottomDrawerSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.backend.viewModels.DataTransferToViewModel
import com.den.steward.helper.combine
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.transactionFields.TransactionAmountField
import com.den.steward.ui.components.transactionFields.TransactionDateField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionFields.TransactionLabelField
import com.den.steward.ui.components.transactionFields.TransactionNoteField
import com.den.steward.ui.components.transactionFields.TransactionTimeField
import com.den.steward.ui.components.transactionbuttons.TransactionButtons

@Composable
fun TransactionBottomDrawerSheet(
    transactionType: TransactionType,
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSubmit: (dataTransferToViewModel: DataTransferToViewModel) -> Unit
) {
    // Tie states to the transactionType so they reset when switching types
    // Label States
    val labelState = remember(transactionType) { TextFieldState() }
    val displayedLabelState = remember(transactionType) { mutableStateOf("") }
    val wasLabelSuccess = remember(transactionType) { mutableStateOf<TransactionFieldState>(TransactionFieldState.Initial) }

    // Note States
    val noteState = remember(transactionType) { TextFieldState() }
    val displayedNoteState = remember(transactionType) { mutableStateOf("") }

    // Amount States
    val amountState = remember(transactionType) { TextFieldState() }
    val displayedAmountState = remember(transactionType) { mutableStateOf("") }
    val wasAmountSuccess = remember(transactionType) { mutableStateOf<TransactionFieldState>(TransactionFieldState.Initial) }

    val createdAt = remember(transactionType) { mutableStateOf(System.currentTimeMillis().toLocalDateTime()) }
    val createdAtLocalDate = remember(transactionType) { mutableStateOf(createdAt.value.toLocalDate()) }
    val createdAtLocalTime = remember(transactionType) { mutableStateOf(createdAt.value.toLocalTime()) }

    val reset = {
        // Reset fields for next use
        displayedAmountState.value = ""
        displayedLabelState.value = ""
        displayedNoteState.value = ""

        wasAmountSuccess.value = TransactionFieldState.Initial
        wasLabelSuccess.value = TransactionFieldState.Initial

        labelState.clearText()
        amountState.clearText()
        noteState.clearText()
    }

    BottomDrawerSheet(
        title = stringResource(id = transactionType.label),
        description = stringResource(id = transactionType.description),
        show = show,
        transactionType = transactionType,
        onDismissRequest = {
            // 1. Reset fields for next use
            reset()

            // 2. Dismiss Bottom Drawer Sheet
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionLabelField(
                title = "Title",
                description = "Give your ${stringResource(transactionType.label).lowercase()} a label",
                state = labelState,
                displayText = displayedLabelState,
                placeholder = "label...",
                colorResId = transactionType.color,
                wasSuccess = wasLabelSuccess
            )
            TransactionAmountField(
                state = amountState,
                placeholder = "0.0",
                wasSuccess = wasAmountSuccess,
                displayState = displayedAmountState
            )
            TransactionNoteField(
                title = "Note",
                description = "Add more details (optional)",
                state = noteState,
                displayText = displayedNoteState,
                placeholder = "note...",
                colorResId = transactionType.color,
            )

            TransactionDateField(
                title = stringResource(transactionType.label),
                color = colorResource(transactionType.color),
                localDateState = createdAtLocalDate
            )

            TransactionTimeField(
                title = stringResource(transactionType.label),
                color = colorResource(transactionType.color),
                localTimeState = createdAtLocalTime
            )

            TransactionButtons(
                modifier = Modifier.padding(vertical = 16.dp),
                transactionType = transactionType,
                isErrors = wasLabelSuccess.value is TransactionFieldState.Error ||
                        wasAmountSuccess.value is TransactionFieldState.Error
            ) {
                if (displayedAmountState.value.isNotEmpty() && displayedLabelState.value.isNotEmpty()) {
                    onSubmit(
                        DataTransferToViewModel(
                            transactionType = transactionType,
                            label = displayedLabelState.value,
                            amount = displayedAmountState.value,
                            note = displayedNoteState.value,
                            createdAt = createdAtLocalDate.value combine (createdAtLocalTime.value)
                        )
                    )

                    // Reset fields for next use
                    reset()

                    onDismissRequest()
                }
                
                if (displayedLabelState.value.isEmpty()) {
                    wasLabelSuccess.value = TransactionFieldState.Error
                }
                if (displayedAmountState.value.isEmpty()) {
                    wasAmountSuccess.value = TransactionFieldState.Error
                }
            }
        }
    }
}

@Composable
fun FulfillmentBottomDrawerSheet(
    transactionType: TransactionType,
    show: Boolean,
    onDismissRequest: () -> Unit
) {
    BottomDrawerSheet(
        title = stringResource(id = transactionType.label),
        description = stringResource(id = transactionType.description),
        show = show,
        onDismissRequest = onDismissRequest,
    ) {

    }
}
