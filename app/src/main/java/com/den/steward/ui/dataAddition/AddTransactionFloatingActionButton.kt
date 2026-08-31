// Glory be to LORD our GOD
package com.den.steward.ui.dataAddition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheetItem
import com.den.steward.ui.components.transactionFields.TransactionAffectAmount
import com.den.steward.ui.components.transactionFields.TransactionAmountField
import com.den.steward.ui.components.transactionFields.TransactionDateField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionFields.TransactionLabelField
import com.den.steward.ui.components.transactionFields.TransactionNoteField
import com.den.steward.ui.components.transactionFields.TransactionPaymentMethodField
import com.den.steward.ui.components.transactionFields.TransactionRecurrenceField
import com.den.steward.ui.components.transactionFields.TransactionTimeField
import com.den.steward.ui.components.transactionbuttons.TransactionButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionFloatingActionButton(
    dataAdditionViewModel: DataAdditionViewModel
) {
    // The state of data addition for textField, validation and button state
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()

    FloatingActionButton(
        onClick = { dataAdditionViewModel.updateShowTransactionTypeBottomSheet(true) },
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
        show = dataAdditionState.showTransactionTypeBottomSheet,
        onDismissRequest = dataAdditionViewModel::onBottomDrawerSheetDismiss,
    ) {
        dataAdditionState.coreEntries.forEach { type ->
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
                dataAdditionViewModel.onBottomDrawerSheetItemClick(type)
            }
        }
    }

    // 2. Show the Transaction Bottom Drawer Sheet
    TransactionBottomDrawerSheet(
        dataAdditionState = dataAdditionState,
        dataAdditionViewModel = dataAdditionViewModel,
    )
}


@Composable
fun TransactionBottomDrawerSheet(
    dataAdditionViewModel: DataAdditionViewModel,
    dataAdditionState: DataAdditionState,
) {
    val type = dataAdditionState.selectedTransactionType ?: return


    BottomDrawerSheet(
        title = stringResource(id = type.label),
        description = stringResource(id = type.description),
        show = dataAdditionState.showTransactionAdditionBottomSheet,
        transactionType = type,
        onDismissRequest = dataAdditionViewModel::onTransactionBottomDrawerSheetDismiss
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionLabelField(
                title = "Title",
                description = "Give your ${stringResource(type.label).lowercase()} a label",
                state = dataAdditionState.label,
                displayText = dataAdditionState.currentLabel,
                onDisplayTextChange = dataAdditionViewModel::updateCorrectLabel,
                updateWasSuccess = dataAdditionViewModel::updateIsLabelCorrect,
                placeholder = "label...",
                colorResId = type.color,
                wasSuccess = dataAdditionState.isLabelCorrect
            )
            TransactionAmountField(
                state = dataAdditionState.amount,
                placeholder = "0.0",
                isAmountCorrect = dataAdditionState.isAmountCorrect,
                updateIsAmountCorrect = dataAdditionViewModel::updateIsAmountCorrect,
                displayState = dataAdditionState.currentAmount,
                updateDisplayState = dataAdditionViewModel::updateCorrectAmount
            )

            if (
                type !in listOf(
                    TransactionType.GOAL,
                    TransactionType.ATTAIN,
                    TransactionType.ACHIEVEMENT
                )
            ) {
                TransactionAffectAmount(
                    colorResId = type.color,
                    isAffectingAmount = dataAdditionState.isAffectingAmount,
                    onCheckedChange = dataAdditionViewModel::updateIsAffectingAmount
                )
            }

            TransactionNoteField(
                title = "Note",
                description = "Add more details (optional)",
                state = dataAdditionState.note,
                displayText = dataAdditionState.currentNote,
                onDisplayTextChange = dataAdditionViewModel::updateCorrectNote,
                placeholder = "note...",
                colorResId = type.color,
            )

            TransactionDateField(
                title = stringResource(type.label),
                colorResId = type.color,
                localDateState = dataAdditionState.localDateCreatedAt,
                onLocalDateChange = dataAdditionViewModel::updateLocalDateCreatedAt
            )

            TransactionTimeField(
                title = stringResource(type.label),
                colorResId = type.color,
                localTime = dataAdditionState.localTimeCreatedAt,
                onLocalTimeChange = dataAdditionViewModel::updateLocalTimeCreatedAt
            )

            // Only show payment method field for non-goal transactions
            if (type != TransactionType.GOAL) {
                TransactionPaymentMethodField(
                    colorResId = type.color,
                    selectedPaymentMethod = dataAdditionState.paymentMethod,
                    onPaymentMethodChange = dataAdditionViewModel::updatePaymentMethod
                )
            }


            // Only show recurrence field for goal transactions
            if (type == TransactionType.GOAL) {
                TransactionRecurrenceField(
                    colorResId = type.color,
                    startedAt = dataAdditionState.startAt,
                    endAt = dataAdditionState.endAt,
                    recurrence = dataAdditionState.recurrence,
                    isStartNotEqualToEndDateTime = dataAdditionState.isStartNotEqualToEndDateTime,
                    onStartTimeChange = dataAdditionViewModel::updateStartAt,
                    onEndTimeChange = dataAdditionViewModel::updateEndAt,
                    onRecurrenceChange = dataAdditionViewModel::updateRecurrence,
                    onIsStartNotEqualToEndDateTimeChange = dataAdditionViewModel::updateIsStartNotEqualToEndDateTime
                )
            }

            TransactionButtons(
                colorResId = type.color,
                modifier = Modifier.padding(vertical = 16.dp),
                transactionType = type,
                isErrors = dataAdditionState.isLabelCorrect is TransactionFieldState.Error ||
                        dataAdditionState.isAmountCorrect is TransactionFieldState.Error ||
                        (if (type == TransactionType.GOAL) 
                            dataAdditionState.isStartNotEqualToEndDateTime is TransactionFieldState.Error 
                        else false),
                onClick = dataAdditionViewModel::addCoreEntriesTransaction
            )
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
