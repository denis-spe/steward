package com.den.steward.helper

import java.time.LocalDate
import java.time.ZoneId

fun getStartOfDayMillis(dayOffset: Long = 0): Long {
    val zone = ZoneId.systemDefault()
    return LocalDate.now(zone)
        .plusDays(dayOffset)
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
}