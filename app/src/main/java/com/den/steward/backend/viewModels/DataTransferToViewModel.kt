package com.den.steward.backend.viewModels

import com.den.steward.backend.dataStructure.TransactionType

data class DataTransferToViewModel(
    val transactionType: TransactionType,
    val label: String,
    val amount: String,
    val note: String,
    val createdAt: Long
)