package com.den.steward.backend.dataStructure

sealed class GoalRepeat() {
    data object NONE : GoalRepeat()
    data object DAILY : GoalRepeat()
    data object WEEKLY : GoalRepeat()
    data object MONTHLY : GoalRepeat()
    data object YEARLY : GoalRepeat()
    data class Custom(val days: List<Int>) : GoalRepeat()

    val name: String
        get() = when (this) {
            is NONE -> "None"
            is DAILY -> "Daily"
            is WEEKLY -> "Weekly"
            is MONTHLY -> "Monthly"
            is YEARLY -> "Yearly"
            is Custom -> "Custom"
        }

    val onSchedule: Long
        get() = when (this) {
            is NONE -> 0L
            is DAILY -> 86400000L
            is WEEKLY -> 604800000L
            is MONTHLY -> 2592000000L
            is YEARLY -> 31536000000L
            is Custom -> this.days.sumOf { it * 86400000L }
        }

}