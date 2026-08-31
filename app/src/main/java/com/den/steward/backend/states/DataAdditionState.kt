// Glory be to LORD GOD the Creator of heaven and earth
package com.den.steward.backend.states

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Immutable
data class DataAdditionState(

    // Selected transaction type
    val selectedTransactionType: TransactionType? = null,

    // Show transaction addition bottom sheet
    val showTransactionAdditionBottomSheet: Boolean = false,

    // Show transaction type bottom sheet
    val showTransactionTypeBottomSheet: Boolean = false,

    // Core transaction types
    val coreEntries: List<TransactionType> = listOf(TransactionType.EARNINGS,
            TransactionType.EXPENSE,
            TransactionType.LENT,
            TransactionType.DEBT,
            TransactionType.SAVINGS,
            TransactionType.GOAL
    ),

    // Adjustment transaction types
    val adjustmentEntries: List<TransactionType> = listOf(
        TransactionType.REPAYMENT,
        TransactionType.REFUND,
        TransactionType.ATTAIN,
    ),

    // Transaction label
    val label: TextFieldState = TextFieldState(),
    val currentLabel: String = "",
    val isLabelCorrect: TransactionFieldState = TransactionFieldState.Initial,

    // Transaction amount
    val amount: TextFieldState = TextFieldState(),
    val currentAmount: String = "",
    val isAmountCorrect: TransactionFieldState = TransactionFieldState.Initial,

    // Transaction note
    val note: TextFieldState = TextFieldState(),
    val currentNote: String = "",

    // Transaction date and time created at
    val localDateCreatedAt: LocalDate = LocalDate.now(),
    val localTimeCreatedAt: LocalTime = LocalTime.now(),
    val localDateTimeCreatedAt: LocalDateTime = localDateCreatedAt.atTime(localTimeCreatedAt),

    // Transaction payment method
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,

    // Transaction affect amount
    val isAffectingAmount: Boolean = true,

    // Goal starting and ending local date time
    val startAt: LocalDateTime = LocalDateTime.now(),
    val endAt: LocalDateTime = LocalDateTime.now(),
    val recurrence: RecurrencePattern = RecurrencePattern.NONE,
    val isStartNotEqualToEndDateTime: TransactionFieldState = TransactionFieldState.Initial,
)
