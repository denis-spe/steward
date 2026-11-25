// Bless be the name of LORD GOD
package com.den.steward.backend.states.goalDetailScreenState

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction

@Immutable
data class GoalData(
    val attain: List<Transaction> = emptyList(),
    val achieve: List<Transaction> = emptyList()
)
