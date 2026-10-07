// Glory be to the LORD our GOD
package com.den.steward.backend.states

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.helper.formatResult
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.SelectedIcon
import com.den.steward.ui.components.icons
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import java.time.LocalDate
import java.time.LocalTime

@Immutable
data class DataUpdateState (
    // Selected transaction
    val selectedTransaction: Transaction? = null,

    // Transaction label
    val label: TextFieldState = TextFieldState(
        initialText = selectedTransaction?.getLabel ?: ""
    ),
    val currentLabel: String = selectedTransaction?.getLabel ?: "",
    val isLabelCorrect: TransactionFieldState = TransactionFieldState.Initial,

    // Transaction amount
    val amount: TextFieldState = TextFieldState(
        initialText = selectedTransaction?.getAmountOrValue?.formatResult ?: "0.0"
    ),
    val currentAmount: String = selectedTransaction?.getAmountOrValue?.toString() ?: "",
    val isAmountCorrect: TransactionFieldState = TransactionFieldState.Initial,

    val isFulfillmentValid: TransactionFieldState = TransactionFieldState.Initial,

    // Transaction note
    val note: TextFieldState = TextFieldState(
        initialText = selectedTransaction?.getNote ?: ""
    ),
    val currentNote: String = selectedTransaction?.getNote ?: "",

    // Transaction payment method
    val paymentMethod: PaymentMethod = selectedTransaction?.getPaymentMethodOrNull ?: PaymentMethod.CASH,

    // Transaction affect amount
    val isAffectingAmount: Boolean = selectedTransaction?.let {
        when(it.getAffectedAmount) {
            Affected.AFFECTED -> true
            Affected.NEUTRAL -> false
            else -> null
        }
    } ?: false,

    // Show the drawer
    val showBottomSheet: Boolean = false,

    // Is saving transaction
    val isSaving: Boolean = false,

    // Selected icon
    val selectedIcon: SelectedIcon = run {
        val iconRes = selectedTransaction?.getIcon ?: SelectedIcon().icon
        val iconName = icons.entries.firstOrNull { (_, v) -> v == iconRes }?.key ?: SelectedIcon().name
        SelectedIcon(
            name = iconName,
            icon = iconRes
        )
    },
    val localTimeCreatedAt: LocalTime = selectedTransaction?.createdAt
        ?.toLocalDateTime()?.toLocalTime() ?: LocalTime.now(),
    val localDateCreatedAt: LocalDate = selectedTransaction?.createdAt
        ?.toLocalDateTime()?.toLocalDate() ?: LocalDate.now(),

    val fromPayment: PaymentMethod = if (selectedTransaction is Transaction.Ramping) {
        selectedTransaction.from
    } else {
        PaymentMethod.CASH
    },

    val toPaymentMethod: PaymentMethod = if (selectedTransaction is Transaction.Ramping) {
        selectedTransaction.to
    } else {
        PaymentMethod.CASH
    },
)