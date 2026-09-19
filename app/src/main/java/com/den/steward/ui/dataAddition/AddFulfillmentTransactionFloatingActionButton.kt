// Glory be to LORD GOD of hosts
package com.den.steward.ui.dataAddition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheetItem

import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.states.DataState
import com.den.steward.ui.components.transactionFields.TransactionAffectAmount
import com.den.steward.ui.components.transactionFields.TransactionAmountField
import com.den.steward.ui.components.transactionFields.TransactionDateField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionFields.TransactionNoteField
import com.den.steward.ui.components.transactionFields.TransactionPaymentMethodField
import com.den.steward.ui.components.transactionFields.TransactionTimeField
import com.den.steward.ui.components.transactionbuttons.TransactionButtons
import com.den.steward.ui.theme.ExtendedTheme
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFulfillmentTransactionFloatingActionButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    dataAdditionViewModel: DataAdditionViewModel
) {
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()

    FloatingActionButton(
        onClick = { dataAdditionViewModel.updateShowFulfillmentTransactionTypeBottomSheet(true) },
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        containerColor = ExtendedTheme.colors.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Icon(
            imageVector = Icons.Rounded.AssignmentTurnedIn,
            contentDescription = "Add Fulfillment"
        )
    }

    BottomDrawerSheet(
        title = "Fulfillment",
        description = "Choose the type of fulfillment you're adding",
        show = dataAdditionState.showFulfillmentTransactionTypeBottomSheet,
        onDismissRequest = { dataAdditionViewModel.updateShowFulfillmentTransactionTypeBottomSheet(false) },
    ) {
        TransactionType.entries.filter {
            it == TransactionType.REPAYMENT ||
                    it == TransactionType.SETTLEMENT ||
                    it == TransactionType.ATTAIN
        }.forEach { type ->
            BottomDrawerSheetItem(
                title = stringResource(id = type.label),
                description = stringResource(id = type.description),
                icon = {
                    Icon(
                        painter = painterResource(id = type.icon),
                        contentDescription = stringResource(id = type.label),
                        tint = colorResource(type.color)
                    )
                },
                onClick = {
                    dataAdditionViewModel.updateSelectedFulfillmentTransactionType(type)
                    dataAdditionViewModel.updateShowFulfillmentTransactionAdditionBottomSheet(true)
                    dataAdditionViewModel.updateShowFulfillmentTransactionTypeBottomSheet(false)
                },
            )
        }
    }

    // Show the transaction addition bottom sheet
    FulfillmentTransactionBottomDrawerSheet(
        dataAdditionViewModel = dataAdditionViewModel,
        dataAdditionState = dataAdditionState
    )
}

@Composable
fun FulfillmentTransactionBottomDrawerSheet(
    dataAdditionViewModel: DataAdditionViewModel,
    dataAdditionState: DataAdditionState,
) {
    val selectedTransactionType = dataAdditionState.selectedFulfillmentTransactionType ?: return
    
    // Map fulfillment type to parent transaction type
    val parentType = when (selectedTransactionType) {
        TransactionType.REPAYMENT -> TransactionType.LENT
        TransactionType.SETTLEMENT -> TransactionType.DEBT
        TransactionType.ATTAIN -> TransactionType.GOAL
        else -> null
    }

    val transactions by remember(parentType) {
        if (parentType != null) {
            dataAdditionViewModel.fetchTransactionByType(parentType)
        } else {
            MutableStateFlow(DataState.Success(emptyList<Transaction>()))
        }
    }.collectAsStateWithLifecycle()

    val transactionValidationState = when {
        dataAdditionState.isFulfillmentValid is TransactionFieldState.Error -> {
            dataAdditionState.isFulfillmentValid
        }

        dataAdditionState.isAmountCorrect is TransactionFieldState.Error -> {
            dataAdditionState.isAmountCorrect
        }

        else -> {
            TransactionFieldState.Initial
        }
    }

    BottomDrawerSheet(
        title = stringResource(id = selectedTransactionType.label),
        description = stringResource(id = selectedTransactionType.description),
        show = dataAdditionState.showFulfillmentTransactionAdditionBottomSheet,
        transactionType = selectedTransactionType,
        onDismissRequest = dataAdditionViewModel::reset
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionAmountField(
                state = dataAdditionState.amount,
                placeholder = "0.0",
                isAmountCorrect = transactionValidationState,
                updateIsAmountCorrect = dataAdditionViewModel::updateIsAmountCorrect,
                displayState = dataAdditionState.currentAmount,
                updateDisplayState = dataAdditionViewModel::updateCorrectAmount,
                transactionName = parentType?.let { stringResource(id = it.label) },
                transactions = when(val state = transactions) {
                    is DataState.Success -> state.data
                    else -> emptyList()
                },
                colorResId = selectedTransactionType.color,
                onSetItem = { transaction ->
                    dataAdditionViewModel.updateSelectedParentTransaction(transaction)
                    dataAdditionViewModel.updateIsFulfillmentValid(TransactionFieldState.Initial)
                },
            )

            if (
                selectedTransactionType !in listOf(
                    TransactionType.GOAL,
                    TransactionType.ATTAIN,
                    TransactionType.ACHIEVEMENT
                )
            ) {
                TransactionAffectAmount(
                    colorResId = selectedTransactionType.color,
                    isAffectingAmount = dataAdditionState.isAffectingAmount,
                    onCheckedChange = dataAdditionViewModel::updateIsAffectingAmount
                )
                TransactionNoteField(
                    title = "Note",
                    description = "Add more details (optional)",
                    state = dataAdditionState.note,
                    displayText = dataAdditionState.currentNote,
                    onDisplayTextChange = dataAdditionViewModel::updateCorrectNote,
                    placeholder = "note...",
                    colorResId = selectedTransactionType.color,
                )

                TransactionDateField(
                    title = stringResource(selectedTransactionType.label),
                    colorResId = selectedTransactionType.color,
                    localDateState = dataAdditionState.localDateCreatedAt,
                    onLocalDateChange = dataAdditionViewModel::updateLocalDateCreatedAt
                )

                TransactionTimeField(
                    title = stringResource(selectedTransactionType.label),
                    colorResId = selectedTransactionType.color,
                    localTime = dataAdditionState.localTimeCreatedAt,
                    onLocalTimeChange = dataAdditionViewModel::updateLocalTimeCreatedAt
                )

                TransactionPaymentMethodField(
                    colorResId = selectedTransactionType.color,
                    selectedPaymentMethod = dataAdditionState.paymentMethod,
                    onPaymentMethodChange = dataAdditionViewModel::updatePaymentMethod
                )
            }

            TransactionButtons(
                colorResId = selectedTransactionType.color,
                modifier = Modifier.padding(vertical = 16.dp),
                transactionType = selectedTransactionType,
                isErrors = dataAdditionState.isAmountCorrect is TransactionFieldState.Error ||
                        dataAdditionState.isFulfillBtnClick && dataAdditionState.selectedParentTransaction == null ||
                        dataAdditionState.isFulfillmentValid is TransactionFieldState.Error ||
                        (if (selectedTransactionType == TransactionType.GOAL)
                            dataAdditionState.isStartNotEqualToEndDateTime is TransactionFieldState.Error
                        else false),
                isLoading = dataAdditionState.isSaving,
                onClick = dataAdditionViewModel::addFulfillmentTransaction
            )
        }
    }
}