package com.den.steward.ui.components.bottomDrawerSheet

import com.den.steward.backend.dataStructure.TransactionType

data class BottomSheetDataSubmitted(
    val transactionType: TransactionType,
    val label: String,
    val amount: String,
    val note: String
)
