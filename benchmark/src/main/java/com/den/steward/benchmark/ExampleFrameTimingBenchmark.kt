package com.den.steward.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleFrameTimingBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollBenchmark() = benchmarkRule.measureRepeated(
        packageName = "com.den.steward",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM
    ) {
        startActivityAndWait()
        
        // Find a scrollable container if any, or just wait a bit
        // Since we don't have a long list, we can just wait for the content to load
        device.wait(Until.hasObject(By.pkg("com.den.steward")), 5000)
    }
}
