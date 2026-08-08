// Grace and truth came through JESUS
package com.den.steward.ui.components.bottomDrawerSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.ui.components.transactionFields.TransactionAmountField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionFields.TransactionLabelField
import com.den.steward.ui.components.transactionFields.TransactionNoteField
import com.den.steward.ui.components.transactionbuttons.TransactionButtons

@Composable
fun TransactionBottomDrawerSheet(
    transactionType: TransactionType,
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSubmit: (bottomSheetDataSubmitted: BottomSheetDataSubmitted) -> Unit
) {
    // Label States
    val labelState = rememberTextFieldState()
    val displayedLabelState = remember { mutableStateOf("") }
    val wasLabelSuccess = remember { mutableStateOf<TransactionFieldState>(TransactionFieldState.Initial) }

    // Note States
    val noteState = rememberTextFieldState()
    val displayedNoteState = remember { mutableStateOf("") }

    // Amount States
    val amountState = rememberTextFieldState()
    val displayedAmountState = remember { mutableStateOf("") }
    val wasAmountSuccess = remember { mutableStateOf<TransactionFieldState>(TransactionFieldState.Initial) }

    BottomDrawerSheet(
        title = stringResource(id = transactionType.label),
        description = stringResource(id = transactionType.description),
        show = show,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionLabelField(
                title = "Label",
                description = "Give your ${stringResource(transactionType.label).lowercase()} a name",
                state = labelState,
                displayText = displayedLabelState,
                placeholder = "label...",
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

            val buttonLabel = when (transactionType) {
                TransactionType.EARNINGS -> "Earned"
                TransactionType.EXPENSE -> "Spent"
                TransactionType.LOAN -> "Lent"
                TransactionType.DEBT -> "Borrowed"
                TransactionType.GOAL -> "Save"
                else -> "Submit"
            }

            TransactionButtons(
                label = buttonLabel,
                modifier = Modifier.padding(vertical = 16.dp),
                transactionType = transactionType
            ) {
                if (displayedAmountState.value.isNotEmpty() && displayedLabelState.value.isNotEmpty()) {
                    onSubmit(
                        BottomSheetDataSubmitted(
                            transactionType = transactionType,
                            label = displayedLabelState.value,
                            amount = displayedAmountState.value,
                            note = displayedNoteState.value
                        )
                    )
                    // Reset fields for next use
                    // displayedAmountState.value = ""
                    // displayedLabelState.value = ""
                    // displayedNoteState.value = ""
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
        onDismissRequest = onDismissRequest
    ) {
        // Implementation for Repayments, Refunds, and Goal Attainment
    }
}
