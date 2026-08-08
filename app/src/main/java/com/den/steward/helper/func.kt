// Glory be to name of LORD GOD
package com.den.steward.helper

import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

fun getCurrencySymbol(): String {
    val locale = Locale.getDefault()
    val numberFormat = NumberFormat.getCurrencyInstance(locale)
    return numberFormat.currency?.symbol ?: "$"
}

fun getStartOfDayMillis(dayOffset: Long = 0): Long {
    val zone = ZoneId.systemDefault()
    return LocalDate.now(zone)
        .plusDays(dayOffset)
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
}