// Glory be to LORD our GOD
package com.den.steward.ui.dataUpdate

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataUpdateState
import com.den.steward.ui.components.SelectedIcon
import com.den.steward.ui.components.transactionFields.TransactionFieldState

@Immutable
interface UpdateTransactionInf {
    val dataUpdateState: DataUpdateState
    val updateCorrectLabel: (displayLabel: String) -> Unit
    val updateIsAmountCorrect: (transactionFieldState: TransactionFieldState) -> Unit
    val updateIsAffectingAmount: (isAffectingAmount: Boolean) -> Unit
    val updateSelectedIcon: (selectedIcon: SelectedIcon) -> Unit
    val updateIsLabelCorrect: (transactionFieldState: TransactionFieldState) -> Unit
    val updateCorrectNote: (displayNote: String) -> Unit
    val updateCorrectAmount: (displayAmount: String) -> Unit
    val updatePaymentMethod: (paymentMethod: PaymentMethod) -> Unit
    val reset: () -> Unit
    val onTransactionUpdate: () -> Unit
    val updateSelectedTransactionForUpdate: (transaction: Transaction?) -> Unit
}