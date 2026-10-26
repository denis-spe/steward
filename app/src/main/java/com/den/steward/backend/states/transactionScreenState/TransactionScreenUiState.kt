// Glory be to the LORD GOD of hosts
package com.den.steward.backend.states.transactionScreenState

import com.den.steward.backend.entitles.Transaction

data class TransactionScreenUiState(
    val selectedTransactionToDelete: Transaction? = null,
    val openDeleteDialog: Boolean = false
)
