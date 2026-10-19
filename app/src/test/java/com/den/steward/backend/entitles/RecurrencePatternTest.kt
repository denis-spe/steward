package com.den.steward.backend.entitles

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek

class RecurrencePatternTest {

    @Test
    fun `entries should not be empty and should not contain nulls`() {
        val entries = RepeatModeType.entries
        assertTrue("Entries should not be empty", entries.isNotEmpty())
        entries.forEach { pattern ->
            assertNotNull("Pattern should not be null", pattern)
            assertNotNull("Pattern name should not be null", pattern.name)
        }
    }

    @Test
    fun `custom pattern onSchedule should calculate correct delay for selected day`() {
        val pattern = RepeatMode(
            repeatModeType = RepeatModeType.Schedule,
            weekDays = listOf(DayOfWeek.FRIDAY)
        )
        val schedule = pattern.onSchedule
        assertTrue("Schedule delay should be greater than 0", schedule > 0L)
    }
}
