package com.den.steward.backend.viewModels

import com.den.steward.backend.dataStructure.PaymentMethod
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.helper.toEpochMillis
import java.time.LocalDateTime

data class DataTransferToViewModel(
    val transactionType: TransactionType,
    val label: String,
    val amount: String,
    val note: String,
    val createdAt: Long,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val endAt: Long = LocalDateTime.now().toEpochMillis(),
    val startedAt: Long = LocalDateTime.now().toEpochMillis(),
)