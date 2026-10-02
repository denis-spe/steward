// Bless be the name of the LORD
package com.den.steward.backend.states.yesterdayTabState

import com.den.steward.backend.entitles.Transaction

data class YesterdayUiState(
    val selectedTransactionToDelete: Transaction? = null,
    val selectedTransactionToEdit: Transaction? = null,
    val openDeleteDialog: Boolean = false,
    val openEditDialog: Boolean = false
)
