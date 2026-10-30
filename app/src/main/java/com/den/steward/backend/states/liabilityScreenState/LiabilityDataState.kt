package com.den.steward.backend.states.liabilityScreenState

import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Stable
data class LiabilityDataState(
    val liabilityTransactions: ImmutableMap<String, List<Transaction>> = persistentMapOf(),
    val liabilityMainSummary: LiabilityMainSummary = LiabilityMainSummary()
)
