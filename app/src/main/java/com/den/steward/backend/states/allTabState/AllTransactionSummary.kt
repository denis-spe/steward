package com.den.steward.backend.states.allTabState

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDate

@Immutable
data class AllTransactionSummary(
    val flow: Double = 0.0,
    val transactionSize: Int = 0,
    val totalReceived: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalSavings: Double = 0.0,
    val dayCountOfTransaction: ImmutableMap<LocalDate, Int> = persistentMapOf()
)