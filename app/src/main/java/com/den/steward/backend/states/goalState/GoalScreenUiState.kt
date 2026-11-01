// Glory be to name of the LORD GOD
package com.den.steward.backend.states.goalState

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction

@Stable
data class GoalScreenUiState(
    val selectedTransactionToDelete: Transaction? = null,
    val openDeleteDialog: Boolean = false,
    val openDeleteAllDialog: Boolean = false,
    val searchTextState: TextFieldState = TextFieldState(),
)
