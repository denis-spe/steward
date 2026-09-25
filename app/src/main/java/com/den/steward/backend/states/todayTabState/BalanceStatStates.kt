// Glory be to LORD our GOD
package com.den.steward.backend.states.todayTabState

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.PaymentMethod
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class BalanceStatStates(
    val flow: Double = 0.0,
    val paymentState: ImmutableMap<PaymentMethod, Double> = persistentMapOf()
)