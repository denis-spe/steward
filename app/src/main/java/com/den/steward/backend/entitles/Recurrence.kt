// Glory be to the LORD GOD of hosts
package com.den.steward.backend.entitles

import com.den.steward.helper.nextTriggerMillisByWeekDay
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

sealed interface Recurrence {
    val name: String
    val toMap: Map<String, Any>

    /** Next occurrence strictly after [after], computed from [anchor]. Null = never again. */
    fun next(after: LocalDateTime, anchor: LocalDateTime): LocalDateTime?

    data object Once : Recurrence {
        override val name: String = "Once"
        override val toMap: Map<String, Any> = mapOf("type" to "Once")

        override fun next(after: LocalDateTime, anchor: LocalDateTime): LocalDateTime? =
            anchor.takeIf { it.isAfter(after) }
    }

    /** Daily / Weekly / Monthly / Yearly */
    data class Every(val unit: ChronoUnit) : Recurrence {
        override val name: String = when (unit) {
            ChronoUnit.DAYS -> "Daily"
            ChronoUnit.WEEKS -> "Weekly"
            ChronoUnit.MONTHS -> "Monthly"
            ChronoUnit.YEARS -> "Yearly"
            else -> unit.name
        }

        override val toMap: Map<String, Any> = mapOf(
            "type" to name,
            "unit" to unit.name
        )

        override fun next(after: LocalDateTime, anchor: LocalDateTime): LocalDateTime {
            var n = unit.between(anchor, after).coerceAtLeast(0)
            var candidate = anchor.plus(n, unit)
            while (!candidate.isAfter(after)) candidate = anchor.plus(++n, unit)
            return candidate
        }
    }

    /** Your "Schedule": specific weekdays at the anchor's time of day. */
    data class OnDays(val days: Set<DayOfWeek> = emptySet()) : Recurrence {
        override val name: String = "Schedule"

        override val toMap: Map<String, Any> = mapOf(
            "type" to "Schedule",
            "weekDays" to days.map { it.name }
        )

        override fun next(after: LocalDateTime, anchor: LocalDateTime): LocalDateTime? {
            if (days.isEmpty()) return null
            return generateSequence(after.toLocalDate()) { it.plusDays(1) }
                .map { it.atTime(anchor.toLocalTime()) }
                .firstOrNull { it.dayOfWeek in days && it.isAfter(after) }
        }

        fun toMillis(localTime: LocalTime): List<Long> {
            return days.map { it.nextTriggerMillisByWeekDay(localTime) }
        }
    }

    companion object {
        val Daily: Recurrence = Every(ChronoUnit.DAYS)
        val Weekly: Recurrence = Every(ChronoUnit.WEEKS)
        val Monthly: Recurrence = Every(ChronoUnit.MONTHS)
        val Yearly: Recurrence = Every(ChronoUnit.YEARS)
        val NONE: Recurrence = Once

        val presets: List<Recurrence> = listOf(
            Once, Daily, Weekly, Monthly, Yearly, OnDays(emptySet())
        )

        fun fromMap(raw: Any?): Recurrence {
            if (raw is Map<*, *>) {
                val typeStr = (raw["type"] ?: raw["repeatModeType"] ?: raw["repeatable"]) as? String
                val weekDaysRaw = raw["weekDays"] as? List<*>
                val weekDays = weekDaysRaw?.mapNotNull { item ->
                    when (item) {
                        is String -> runCatching { DayOfWeek.valueOf(item) }.getOrNull()
                        is Number -> runCatching { DayOfWeek.of(item.toInt()) }.getOrNull()
                        else -> null
                    }
                }?.toSet().orEmpty()

                if (!typeStr.isNullOrBlank()) {
                    when (typeStr.lowercase()) {
                        "daily", "days" -> return Daily
                        "weekly", "weeks" -> return Weekly
                        "monthly", "months" -> return Monthly
                        "yearly", "years" -> return Yearly
                        "schedule", "ondays" -> return OnDays(weekDays)
                        "once" -> return Once
                    }
                }
                if (weekDays.isNotEmpty()) {
                    return OnDays(weekDays)
                }
                val unitStr = raw["unit"] as? String
                if (!unitStr.isNullOrBlank()) {
                    val unit = runCatching { ChronoUnit.valueOf(unitStr) }.getOrNull()
                    if (unit != null) return Every(unit)
                }
            } else if (raw is String) {
                when (raw.lowercase()) {
                    "daily" -> return Daily
                    "weekly" -> return Weekly
                    "monthly" -> return Monthly
                    "yearly" -> return Yearly
                    "schedule" -> return OnDays(emptySet())
                    "once" -> return Once
                }
            }
            return NONE
        }
    }
}
