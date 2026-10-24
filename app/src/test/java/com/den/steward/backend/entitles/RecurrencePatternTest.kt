package com.den.steward.backend.entitles

import com.den.steward.backend.workers.GoalWorker
import com.den.steward.helper.nextCycle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId

class RecurrencePatternTest {

    @Test
    fun `presets should not be empty and should have valid names`() {
        val presets = Recurrence.presets
        assertTrue("Presets should not be empty", presets.isNotEmpty())
        presets.forEach { preset ->
            assertNotNull("Preset should not be null", preset)
            assertNotNull("Preset name should not be null", preset.name)
        }
    }

    @Test
    fun `custom pattern OnDays should calculate correct recurrence for selected day`() {
        val pattern = Recurrence.OnDays(days = setOf(DayOfWeek.FRIDAY))
        assertEquals("Schedule", pattern.name)
        assertTrue("Days should contain Friday", pattern.days.contains(DayOfWeek.FRIDAY))
    }

    @Test
    fun `nextCycle should advance weekly goal start and end times`() {
        val now = System.currentTimeMillis()
        val goal = Transaction.Goal(
            id = "test_goal",
            startedAt = now - 604800000L,
            endAt = now,
            repeatable = Recurrence.Weekly,
            attain = listOf(Transaction.Attain(id = "1", value = 10.0))
        )

        val updatedGoal = goal.nextCycle(now)

        assertEquals(now, updatedGoal.startedAt)
        assertTrue("End date should be after startedAt", updatedGoal.endAt > updatedGoal.startedAt)
        assertTrue("Attain should be cleared for next cycle", updatedGoal.attain.isEmpty())
        assertEquals(Recurrence.Weekly, updatedGoal.repeatable)
    }

    @Test
    fun `nextTriggerMillis should return future endAtMillis directly`() {
        val now = System.currentTimeMillis()
        val futureEndAt = now + 86400000L // 1 day in the future

        val trigger = GoalWorker.nextTriggerMillis(futureEndAt, Recurrence.Daily, now = now)
        assertEquals(futureEndAt, trigger)
    }

    @Test
    fun `nextTriggerMillis should return null for Once when deadline passed`() {
        val now = System.currentTimeMillis()
        val pastEndAt = now - 1000L

        val trigger = GoalWorker.nextTriggerMillis(pastEndAt, Recurrence.Once, now = now)
        assertNull("Once recurrence should not schedule next occurrence after deadline", trigger)
    }

    @Test
    fun `nextTriggerMillis should calculate next day for Daily when deadline passed`() {
        val zone = ZoneId.systemDefault()
        val nowLocal = LocalDateTime.of(2025, 3, 1, 10, 0, 0)
        val now = nowLocal.atZone(zone).toInstant().toEpochMilli()

        val anchorLocal = LocalDateTime.of(2025, 3, 1, 9, 0, 0) // 1 hour ago
        val pastEndAt = anchorLocal.atZone(zone).toInstant().toEpochMilli()

        val trigger =
            GoalWorker.nextTriggerMillis(pastEndAt, Recurrence.Daily, now = now, zone = zone)
        assertNotNull(trigger)

        val expectedNextLocal = LocalDateTime.of(2025, 3, 2, 9, 0, 0)
        val expectedTrigger = expectedNextLocal.atZone(zone).toInstant().toEpochMilli()
        assertEquals(expectedTrigger, trigger)
    }

    @Test
    fun `nextTriggerMillis should calculate next matching day for OnDays schedule`() {
        val zone = ZoneId.systemDefault()
        // Suppose now is Monday March 3, 2025 at 10:00 AM
        val nowLocal = LocalDateTime.of(2025, 3, 3, 10, 0, 0)
        val now = nowLocal.atZone(zone).toInstant().toEpochMilli()

        // Goal anchor was Monday March 3, 2025 at 9:00 AM (deadline passed 1 hour ago)
        val pastEndAt =
            LocalDateTime.of(2025, 3, 3, 9, 0, 0).atZone(zone).toInstant().toEpochMilli()

        val recurrence = Recurrence.OnDays(setOf(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))

        val trigger = GoalWorker.nextTriggerMillis(pastEndAt, recurrence, now = now, zone = zone)
        assertNotNull(trigger)

        // Expected next occurrence is Wednesday March 5, 2025 at 9:00 AM
        val expectedNext =
            LocalDateTime.of(2025, 3, 5, 9, 0, 0).atZone(zone).toInstant().toEpochMilli()
        assertEquals(expectedNext, trigger)
    }
}
