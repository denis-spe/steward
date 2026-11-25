// Glory be to the LORD GOD of hosts
package com.den.steward.backend.states.goalDetailScreenState

import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction

@Stable
data class GoalDetailUiState(
    val selectedTransactionToDelete: Transaction? = null,
    val openDeleteDialog: Boolean = false,
    val openDeleteAllDialog: Boolean = false,
)