package com.den.steward.ui.screens.detailScreen.goalDetailScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.fields.TransactionAmountField
import com.den.steward.ui.components.fields.TransactionFieldState
import com.den.steward.ui.components.transactionbuttons.TransactionButtons

@Composable
fun AttainTransactionBottomDrawerSheet(
    amount: TextFieldState,
    selectedTransactionType: TransactionType,
    showFulfillmentTransactionAdditionBottomSheet: Boolean,
    isFulfillmentValid: TransactionFieldState,
    isAmountCorrect: TransactionFieldState,
    updateIsAmountCorrect: (TransactionFieldState) -> Unit,
    currentAmount: String,
    updateCorrectAmount: (String) -> Unit,
    isSaving: Boolean,
    isFulfillBtnClick: Boolean,
    addFulfillmentTransaction: () -> Unit,
    isStartNotEqualToEndDateTime: TransactionFieldState,
    onDismissRequest: () -> Unit
) {

    // Map fulfillment type to parent transaction type
    val parentType = when (selectedTransactionType) {
        TransactionType.REPAYMENT -> TransactionType.LENT
        TransactionType.SETTLEMENT -> TransactionType.DEBT
        TransactionType.ATTAIN -> TransactionType.GOAL
        else -> null
    }


    val transactionValidationState = when {
        isFulfillmentValid is TransactionFieldState.Error -> {
            isFulfillmentValid
        }

        isAmountCorrect is TransactionFieldState.Error -> {
            isAmountCorrect
        }

        else -> {
            TransactionFieldState.Initial
        }
    }

    val selectedTypeLabel = stringResource(id = selectedTransactionType.label)

    BottomDrawerSheet(
        title = selectedTypeLabel,
        description = stringResource(id = selectedTransactionType.description),
        show = showFulfillmentTransactionAdditionBottomSheet,
        transactionType = selectedTransactionType,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionAmountField(
                state = amount,
                placeholder = "0.0",
                isAmountCorrect = transactionValidationState,
                updateIsAmountCorrect = updateIsAmountCorrect,
                displayState = currentAmount,
                updateDisplayState = updateCorrectAmount,
                transactionName = parentType?.let { stringResource(id = it.label) },
                colorResId = selectedTransactionType.color,
            )

            TransactionButtons(
                colorResId = selectedTransactionType.color,
                modifier = Modifier.padding(vertical = 16.dp),
                transactionType = selectedTransactionType,
                isErrors = isAmountCorrect is TransactionFieldState.Error ||
                        isFulfillBtnClick &&
                        isFulfillmentValid is TransactionFieldState.Error ||
                        (selectedTransactionType == TransactionType.GOAL && isStartNotEqualToEndDateTime is TransactionFieldState.Error),
                isLoading = isSaving,
                onClick = addFulfillmentTransaction
            )
        }
    }
}