package com.den.steward.backend.entitles

import com.den.steward.helper.toEpochMillis
import com.den.steward.helper.toLocalDateTime

sealed class RecurrencePattern() {
    data object NONE : RecurrencePattern()
    data object DAILY : RecurrencePattern()
    data object WEEKLY : RecurrencePattern()
    data object MONTHLY : RecurrencePattern()
    data object YEARLY : RecurrencePattern()
    data class Custom(val days: List<Int> = emptyList()) : RecurrencePattern()

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
            Custom()
        )
    }

    val onSchedule: Long
        get() {
            val nowMillis = System.currentTimeMillis()
            val now = nowMillis.toLocalDateTime()

            val nextTime = when (this) {
                is DAILY -> now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                is WEEKLY -> now.plusWeeks(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                is MONTHLY -> now.plusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                is YEARLY -> now.plusYears(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                is Custom -> {
                    if (days.isEmpty()) return 0L
                    val todayIndex = now.dayOfWeek.value % 7
                    val nextDay = days.sorted().find { it > todayIndex } ?: days.minOrNull() ?: todayIndex
                    val daysToAdd = if (nextDay > todayIndex) nextDay - todayIndex else 7 - (todayIndex - nextDay)
                    now.plusDays(daysToAdd.toLong()).withHour(0).withMinute(0).withSecond(0).withNano(0)
                }
                else -> return 0L
            }

            val nextMillis = nextTime.toEpochMillis()
            return if (nextMillis > nowMillis) nextMillis - nowMillis else 0L
        }
}