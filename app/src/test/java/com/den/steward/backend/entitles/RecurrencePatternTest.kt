package com.den.steward.backend.entitles

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecurrencePatternTest {

    @Test
    fun `entries should not be empty and should not contain nulls`() {
        val entries = RecurrencePattern.entries
        assertTrue("Entries should not be empty", entries.isNotEmpty())
        entries.forEach { pattern ->
            assertNotNull("Pattern should not be null", pattern)
            assertNotNull("Pattern name should not be null", pattern.name)
        }
    }
}
