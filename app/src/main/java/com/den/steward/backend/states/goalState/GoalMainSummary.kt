// Holy, holy, holy is the LORD of host, The whole earth is full of his glory
package com.den.steward.backend.states.goalState

import androidx.compose.runtime.Stable

@Stable
data class GoalMainSummary(
    val rate: Double = 0.0,
    val totalCompletedAchievement: Int = 0,
    val totalFailedAchievement: Int = 0,
    val totalCompletedAchievementPercent: Int = 0,
    val totalFailedAchievementPercent: Int = 0
)
