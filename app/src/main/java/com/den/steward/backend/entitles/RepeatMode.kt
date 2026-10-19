package com.den.steward.backend.entitles

import com.den.steward.helper.toEpochMillis
import com.den.steward.helper.toLocalDateTime
import java.time.DayOfWeek

data class RepeatMode(
    val repeatModeType: RepeatModeType? = null,
    val millis: Long = 0L,
    val weekDays: List<DayOfWeek> = emptyList()
) {
    val name: String
        get() = repeatModeType?.name ?: "NONE"

    val onSchedule: Long
        get() {
            if (repeatModeType == null) return 0L
            val nowMillis = System.currentTimeMillis()
            val now = nowMillis.toLocalDateTime()

            val nextTime = when (repeatModeType) {
                RepeatModeType.Weekly -> now.plusWeeks(1).withHour(23).withMinute(59).withSecond(59)
                RepeatModeType.Monthly -> now.plusMonths(1).withHour(23).withMinute(59)
                    .withSecond(59)

                RepeatModeType.Time -> if (millis > 0L) return millis else return 0L
                RepeatModeType.Schedule -> {
                    if (weekDays.isEmpty()) return 0L
                    val todayDay = now.dayOfWeek
                    val minOffset =
                        weekDays.minOfOrNull { (it.value - todayDay.value + 7) % 7 } ?: 0
                    val daysToAdd = if (minOffset == 0) 7 else minOffset
                    now.plusDays(daysToAdd.toLong()).withHour(23).withMinute(59).withSecond(59)
                }
            }

            val nextMillis = nextTime.toEpochMillis()
            return if (nextMillis > nowMillis) nextMillis - nowMillis else 0L
        }

    companion object {
        val NONE = RepeatMode()
    }
}
