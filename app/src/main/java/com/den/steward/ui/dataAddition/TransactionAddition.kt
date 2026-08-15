// Glory be to LORD our GOD
package com.den.steward.ui.dataAddition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.den.steward.backend.dataStructure.PaymentMethod
import com.den.steward.backend.dataStructure.RecurrencePattern
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheetItem
import com.den.steward.backend.viewModels.DataTransferToViewModel
import com.den.steward.helper.combine
import com.den.steward.helper.toEpochMillis
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.transactionFields.TransactionAmountField
import com.den.steward.ui.components.transactionFields.TransactionDateField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionFields.TransactionLabelField
import com.den.steward.ui.components.transactionFields.TransactionNoteField
import com.den.steward.ui.components.transactionFields.TransactionPaymentMethodField
import com.den.steward.ui.components.transactionFields.TransactionRecurrenceField
import com.den.steward.ui.components.transactionFields.TransactionTimeField
import com.den.steward.ui.components.transactionbuttons.TransactionButtons
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAddition(onSubmit: (dataTransferToViewModel: DataTransferToViewModel) -> Unit) {
    val onShow = remember { mutableStateOf(false) }
    val onShowTransaction = remember { mutableStateOf(false) }
    val selectedTransaction = remember { mutableStateOf<TransactionType?>(null) }
    val transactionTypes = remember { TransactionType.entries.filter {
        it == TransactionType.EARNINGS ||
                it == TransactionType.EXPENSE ||
                it == TransactionType.LOAN ||
                it == TransactionType.DEBT ||
                it == TransactionType.SAVINGS ||
                it == TransactionType.GOAL
        }
    }

    FloatingActionButton(
        onClick = { onShow.value = true },
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Transaction"
        )
    }

    // 1. Selection of Transaction Type Bottom Drawer Sheet
    BottomDrawerSheet(
        title = "Transactions",
        description = "Select the type of transaction you're adding",
        show = onShow.value,
        onDismissRequest = { onShow.value = false },
    ) {
        transactionTypes.forEach { type ->
            BottomDrawerSheetItem(
                title = stringResource(id = type.label),
                description = stringResource(id = type.description),
                icon = {
                    Icon(
                        painter = painterResource(id = type.icon),
                        contentDescription = stringResource(id = type.label),
                        tint = colorResource(id = type.color)
                    )
                }
            ) {
                selectedTransaction.value = type
                onShow.value = false // Hide the first sheet immediately
                onShowTransaction.value = true // Then show the second sheet
            }
        }
    }

    // 2. Show the Transaction Bottom Drawer Sheet
    selectedTransaction.value?.let {
        TransactionBottomDrawerSheet(
            transactionType = it,
            show = onShowTransaction.value,
            onDismissRequest = {
                onShowTransaction.value = false
                onShow.value = false
            },
            onSubmit = onSubmit
        )
    }
}


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

    // Date and time States
    val createdAt = remember(transactionType) { mutableStateOf(System.currentTimeMillis().toLocalDateTime()) }
    val createdAtLocalDate = remember(transactionType) { mutableStateOf(createdAt.value.toLocalDate()) }
    val createdAtLocalTime = remember(transactionType) { mutableStateOf(createdAt.value.toLocalTime()) }

    // Payment Method States
    val paymentMethodState = remember(transactionType) { mutableStateOf(PaymentMethod.CASH) }

    // Recurrence States
    val startAt = remember(transactionType) { mutableStateOf(System.currentTimeMillis().toLocalDateTime()) }
    val endAt = remember(transactionType) { mutableStateOf(System.currentTimeMillis().toLocalDateTime()) }
    val recurrence = remember(transactionType) { mutableStateOf<RecurrencePattern>(RecurrencePattern.NONE) }


    val reset = {
        // Reset fields for next use
        displayedAmountState.value = ""
        displayedLabelState.value = ""
        displayedNoteState.value = ""

        startAt.value = System.currentTimeMillis().toLocalDateTime()
        endAt.value = System.currentTimeMillis().toLocalDateTime()
        recurrence.value = RecurrencePattern.NONE

        createdAt.value = System.currentTimeMillis().toLocalDateTime()
        createdAtLocalDate.value = createdAt.value.toLocalDate()
        createdAtLocalTime.value = createdAt.value.toLocalTime()

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
                colorResId = transactionType.color,
                localDateState = createdAtLocalDate
            )

            TransactionTimeField(
                title = stringResource(transactionType.label),
                colorResId = transactionType.color,
                localTimeState = createdAtLocalTime
            )

            // Only show payment method field for non-goal transactions
            if (transactionType != TransactionType.GOAL) {
                TransactionPaymentMethodField(
                    colorResId = transactionType.color,
                    selectedPaymentMethod = paymentMethodState
                )
            }


            // Only show recurrence field for goal transactions
            if (transactionType == TransactionType.GOAL) {
                TransactionRecurrenceField(
                    colorResId = transactionType.color,
                    startedAt = startAt,
                    endAt = endAt,
                    recurrence = recurrence
                )
            }

            TransactionButtons(
                colorResId = transactionType.color,
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
                            createdAt = createdAtLocalDate.value combine (createdAtLocalTime.value),
                            paymentMethod = paymentMethodState.value,
                            startedAt = startAt.value.toEpochMillis(),
                            endAt = endAt.value.toEpochMillis(),
                            repeatable = recurrence.value
                        )
                    )

                    // Reset fields for next use
                    reset()

                    onDismissRequest()
                }

                if (displayedLabelState.value.isEmpty()) {
                    wasLabelSuccess.value = TransactionFieldState.Error("Label cannot be empty")
                }
                if (displayedAmountState.value.isEmpty()) {
                    wasAmountSuccess.value = TransactionFieldState.Error("Amount cannot be empty")
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
