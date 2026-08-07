package com.den.steward.helper

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Returns the current time minus 24 hours in milliseconds.
 */
fun yesterdayMillis(): Long = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()
