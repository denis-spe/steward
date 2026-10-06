// Bless be the LORD GOD of hosts
package com.den.steward.ui.dataUpdate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataUpdateState
import com.den.steward.ui.components.SelectedIcon
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.transactionFields.TransactionAffectAmount
import com.den.steward.ui.components.transactionFields.TransactionAmountField
import com.den.steward.ui.components.transactionFields.TransactionDateField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionFields.TransactionIconSelector
import com.den.steward.ui.components.transactionFields.TransactionLabelField
import com.den.steward.ui.components.transactionFields.TransactionNoteField
import com.den.steward.ui.components.transactionFields.TransactionPaymentMethodField
import com.den.steward.ui.components.transactionFields.TransactionTimeField
import com.den.steward.ui.components.transactionbuttons.TransactionUpdateButtons
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun UpdateTransactionBottomDrawerSheet(
    dataUpdateState: DataUpdateState,
    updateCorrectLabel: (displayLabel: String) -> Unit,
    updateIsLabelCorrect: (transactionFieldState: TransactionFieldState) -> Unit,
    updateIsAffectingAmount: (isAffectingAmount: Boolean) -> Unit,
    updateSelectedIcon: (selectedIcon: SelectedIcon) -> Unit,
    updateIsAmountCorrect: (transactionFieldState: TransactionFieldState) -> Unit,
    updateCorrectNote: (displayNote: String) -> Unit,
    updateCorrectAmount: (displayAmount: String) -> Unit,
    updatePaymentMethod: (paymentMethod: PaymentMethod) -> Unit,
    onTransactionUpdate: () -> Unit,
    onLocalTimeChange: (LocalTime) -> Unit,
    onLocalDateChange: (LocalDate) -> Unit,
    reset: () -> Unit
) {
    val transaction = dataUpdateState.selectedTransaction ?: return
    val transactionType = transaction.type
    val label = stringResource(id = transactionType.label)

    BottomDrawerSheet(
        title = label,
        description = stringResource(id = transactionType.description),
        show = dataUpdateState.showBottomSheet,
        transactionType = transactionType,
        onDismissRequest = reset
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionLabelField(
                title = "Title",
                description = "update your ${label.lowercase()} a label",
                state = dataUpdateState.label,
                displayText = dataUpdateState.currentLabel,
                onDisplayTextChange = updateCorrectLabel,
                updateWasSuccess = updateIsLabelCorrect,
                placeholder = "label...",
                colorResId = transactionType.color,
                wasSuccess = dataUpdateState.isLabelCorrect
            )
            TransactionAmountField(
                state = dataUpdateState.amount,
                placeholder = "0.0",
                isAmountCorrect = dataUpdateState.isAmountCorrect,
                updateIsAmountCorrect = updateIsAmountCorrect,
                displayState = dataUpdateState.currentAmount,
                colorResId = transactionType.color,
                updateDisplayState = updateCorrectAmount,
            )

            if (
                transactionType !in listOf(
                    TransactionType.GOAL,
                    TransactionType.PLAN,
                )
            ) {
                TransactionAffectAmount(
                    colorResId = transactionType.color,
                    isAffectingAmount = dataUpdateState.isAffectingAmount,
                    onCheckedChange = updateIsAffectingAmount
                )
            }

            TransactionNoteField(
                title = "Note",
                description = "Add more details (optional)",
                state = dataUpdateState.note,
                displayText = dataUpdateState.currentNote,
                onDisplayTextChange = updateCorrectNote,
                placeholder = "note...",
                colorResId = transactionType.color,
            )

            TransactionDateField(
                title = label,
                colorResId = transactionType.color,
                localDateState = dataUpdateState.localDateCreatedAt,
                onLocalDateChange = onLocalDateChange
            )

            TransactionTimeField(
                title = label,
                colorResId = transactionType.color,
                localTime = dataUpdateState.localTimeCreatedAt,
                onLocalTimeChange = onLocalTimeChange
            )

            TransactionIconSelector(
                colorResId = transactionType.color,
                selectedIcon = dataUpdateState.selectedIcon,
                onIconSelected = updateSelectedIcon
            )

            // Only show payment method field for non-goal transactions
            if (transactionType != TransactionType.GOAL && transactionType != TransactionType.PLAN) {
                TransactionPaymentMethodField(
                    colorResId = transactionType.color,
                    selectedPaymentMethod = dataUpdateState.paymentMethod,
                    onPaymentMethodChange = updatePaymentMethod
                )
            }

            TransactionUpdateButtons(
                colorResId = transactionType.color,
                modifier = Modifier.padding(vertical = 16.dp),
                transactionType = transactionType,
                isErrors = dataUpdateState.isLabelCorrect is TransactionFieldState.Error ||
                        dataUpdateState.isAmountCorrect is TransactionFieldState.Error,
                isLoading = dataUpdateState.isSaving,
                onClick = onTransactionUpdate
            )
        }
    }
}
