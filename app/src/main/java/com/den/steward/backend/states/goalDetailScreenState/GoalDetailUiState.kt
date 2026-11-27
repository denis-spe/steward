// Glory be to the LORD GOD of hosts
package com.den.steward.backend.states.goalDetailScreenState

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataNotifier
import com.den.steward.ui.components.fields.TransactionFieldState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Stable
data class GoalDetailUiState(
    val selectedTransactionToDelete: Transaction? = null,
    val openDeleteDialog: Boolean = false,
    val openDeleteAllDialog: Boolean = false,

    // Transaction amount
    val amount: TextFieldState = TextFieldState(),
    val currentAmount: String = "",
    val isAmountCorrect: TransactionFieldState = TransactionFieldState.Initial,

    val isFulfillmentValid: TransactionFieldState = TransactionFieldState.Initial,

    // Transaction date and time created at
    val localDateCreatedAt: LocalDate = LocalDate.now(),
    val localTimeCreatedAt: LocalTime = LocalTime.now(),
    val localDateTimeCreatedAt: LocalDateTime = localDateCreatedAt.atTime(localTimeCreatedAt),

    val dataNotifier: DataNotifier? = null,

    // Is saving transaction
    val isSaving: Boolean = false,

    val isFulfillBtnClick: Boolean = false,

    val showFulfillmentTransactionAdditionBottomSheet: Boolean = false,
    val isStartNotEqualToEndDateTime: TransactionFieldState = TransactionFieldState.Initial,

    )