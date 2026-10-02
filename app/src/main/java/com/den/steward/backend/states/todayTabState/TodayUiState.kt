// Glory be to the name of the LORD
package com.den.steward.backend.states.todayTabState

import com.den.steward.backend.entitles.Transaction

data class TodayUiState(
    val selectedTransactionToDelete: Transaction? = null,
    val selectedTransactionToEdit: Transaction? = null,
    val openDeleteDialog: Boolean = false,
    val openEditDialog: Boolean = false
)
