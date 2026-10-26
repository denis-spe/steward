// Bless be the LORD our GOD, The Father of our LORD JESUS
package com.den.steward.backend.states.transactionScreenState

import androidx.compose.runtime.Stable
import com.den.steward.backend.entitles.Transaction
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Stable
data class TransactionDataState(
    val transactions: ImmutableMap<String, List<Transaction>> = persistentMapOf(),
    val transactionMainSummary: TransactionMainSummary
)
