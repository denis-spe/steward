// Glory be to name of the LORD GOD
package com.den.steward.ui.components

import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.den.steward.R
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataNotifier
import com.den.steward.backend.states.DataUpdateState
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.components.fields.TransactionFieldState
import com.den.steward.ui.dataUpdate.UpdateTransactionBottomDrawerSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TransactionViewDeleteUpdateActivity(
    selectedTransactionToDelete: Transaction?,
    selectedTransactionForView: MutableState<Transaction?>,
    dataUpdateState: DataUpdateState,
    swipedState: SwipeToDismissBoxState?,
    onDataNotifier: (DataNotifier?) -> Unit,
    isDeleteDialogOpen: Boolean,
    scope: CoroutineScope,
    onUpdateTransaction: () -> Unit,
    onResetUpdateTransaction: () -> Unit,
    onLocalTimeChangeUpdate: (LocalTime) -> Unit,
    onLocalDateChangeUpdate: (LocalDate) -> Unit,
    updateCurrentNote: (String) -> Unit,
    updateCurrentLabel: (String) -> Unit,
    updateSelectedIcon: (SelectedIcon) -> Unit,
    updatePaymentMethod: (PaymentMethod) -> Unit,
    onExchange: () -> Unit,
    deleteTransaction: () -> Unit,
    updateCurrentAmount: (String) -> Unit,
    updateIsLabelCorrect: (TransactionFieldState) -> Unit,
    updateIsAmountCorrect: (TransactionFieldState) -> Unit,
    updateIsAffectingAmount: (Boolean) -> Unit,
    updateSelectedTransactionToDelete: (transaction: Transaction?) -> Unit,
    updateOpenDeleteDialog: (isDeleteDialogOpen: Boolean) -> Unit
){
    selectedTransactionForView.value?.let { transaction ->
        TransactionViewDialog(
            transaction = transaction,
            onShow = true,
            onDismissRequest = { selectedTransactionForView.value = null }
        )
    }

    DataDeletionDialog(
        transaction = selectedTransactionToDelete,
        onDialogShow = isDeleteDialogOpen,
        onDelete = {

            onDataNotifier(
                DataNotifier(
                    text = "Successfully deleted ${selectedTransactionToDelete?.getLabel?.lowercase()}",
                    icon = selectedTransactionToDelete?.getSelectedIcon?.icon ?: R.drawable.description,
                    amount = selectedTransactionToDelete?.getFormattedAmountOrValue ?: ""
                )
            )

            deleteTransaction()
        },
    ) {
        updateOpenDeleteDialog(false)
        updateSelectedTransactionToDelete(null)

        scope.launch {
            swipedState?.reset()
        }
    }

    UpdateTransactionBottomDrawerSheet(
        dataUpdateState = dataUpdateState,
        updateCorrectNote = updateCurrentNote,
        updateCorrectLabel = updateCurrentLabel,
        updateSelectedIcon = updateSelectedIcon,
        updatePaymentMethod = updatePaymentMethod,
        updateCorrectAmount = updateCurrentAmount,
        updateIsLabelCorrect = updateIsLabelCorrect,
        updateIsAmountCorrect = updateIsAmountCorrect,
        updateIsAffectingAmount = updateIsAffectingAmount,
        onLocalTimeChange = onLocalTimeChangeUpdate,
        onLocalDateChange = onLocalDateChangeUpdate,
        onExchange = onExchange,
        onTransactionUpdate = {
            onUpdateTransaction()
            onDataNotifier(
                DataNotifier(
                    text = "Successfully updated ${dataUpdateState.label.text.toString().lowercase()}",
                    icon = dataUpdateState.selectedIcon.icon,
                    amount = (dataUpdateState.amount.text.toString().toDoubleOrNull() ?: 0.0)
                        .formatToAmount()
                )
            )
            scope.launch {
                swipedState?.reset()
            }
        },
        reset =  {
            onResetUpdateTransaction()
            scope.launch {
                swipedState?.reset()
            }
        }
    )
}