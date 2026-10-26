// Glory be to LORD our GOD
package com.den.steward.backend.states.transactionScreenState

import androidx.compose.runtime.Immutable

@Immutable
data class TransactionMainSummary(
    val netFlow: Double = 0.0,
    val incoming: Double = 0.0,
    val outgoing: Double = 0.0,
    val incomingPercent: Double = 0.0,
    val outgoingPercent: Double = 0.0
)
