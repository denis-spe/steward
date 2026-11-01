// Bless be the LORD my strength who teach every good things
package com.den.steward.backend.states.goalState

import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Stable
data class GoalDataState(
    val goalTransactions: ImmutableMap<String, List<Transaction>> = persistentMapOf(),
    val goalMainSummary: GoalMainSummary = GoalMainSummary()
)
