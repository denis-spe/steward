package com.den.steward.backend.dataStructure

sealed class RecurrencePattern() {
    data object NONE : RecurrencePattern()
    data object DAILY : RecurrencePattern()
    data object WEEKLY : RecurrencePattern()
    data object MONTHLY : RecurrencePattern()
    data object YEARLY : RecurrencePattern()
    data class Custom(val days: List<Int>) : RecurrencePattern()

    val name: String
        get() = when (this) {
            is NONE -> "None"
            is DAILY -> "Daily"
            is WEEKLY -> "Weekly"
            is MONTHLY -> "Monthly"
            is YEARLY -> "Yearly"
            is Custom -> "Custom"
        }
    companion object {
        val entries = listOf(
            NONE,
            DAILY,
            WEEKLY,
            MONTHLY,
            YEARLY,
        )
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