// Bless be the LORD GOD of hosts
package com.den.steward.backend.states.liabilityScreenState

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction

@Immutable
data class LiabilityScreenUiState(
    val selectedTransactionToDelete: Transaction? = null,
    val openDeleteDialog: Boolean = false,
    val openDeleteAllDialog: Boolean = false
)