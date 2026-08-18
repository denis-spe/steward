package com.den.steward.backend.viewModels

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.helper.toEpochMillis
import java.time.LocalDateTime

@Immutable
data class DataTransferToViewModel(
    val transactionType: TransactionType,
    val label: String,
    val amount: String,
    val note: String,
    val createdAt: Long,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val endAt: Long = LocalDateTime.now().toEpochMillis(),
    val startedAt: Long = LocalDateTime.now().toEpochMillis(),
    val repeatable: RecurrencePattern = RecurrencePattern.NONE,
    val isAffectingAmount: Boolean? = null,
)