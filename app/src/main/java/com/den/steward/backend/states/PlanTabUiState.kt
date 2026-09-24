// Glory be to the LORD our GOD
package com.den.steward.backend.states

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import java.time.LocalDate
import java.time.LocalTime

@Immutable
data class PlanTabUiState(
    val label: TextFieldState = TextFieldState(),
    val isLabelCorrect: TransactionFieldState = TransactionFieldState.Initial,

    val amount: TextFieldState = TextFieldState(),
    val isAmountCorrect: TransactionFieldState = TransactionFieldState.Initial,

    val note: TextFieldState = TextFieldState(),

    val isFulfillBtnClick: Boolean = false,
    val  isSaving: Boolean = false,

    val localDateCreatedAt: LocalDate = LocalDate.now(),
    val localTimeCreatedAt: LocalTime = LocalTime.now(),

    val selectedParentTransaction: Transaction? = null,
    val selectedPlanFulfillmentType: TransactionType = TransactionType.EARNINGS,
    val showFulfillmentTransactionTypeBottomSheet: Boolean = false,
    val selectedFulfillmentTransactionType: TransactionType = TransactionType.PLAN_FULFILLMENT,
    val isFulfillmentValid: TransactionFieldState = TransactionFieldState.Initial,
    )
