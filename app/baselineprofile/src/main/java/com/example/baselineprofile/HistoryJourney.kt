package com.example.baselineprofile

import android.annotation.SuppressLint
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class HistoryJourney {

    @get:Rule
    val rule = BaselineProfileRule()

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Benchmark test to measure performance without any compilation optimizations.
     * This test does not utilize any compilation mode and runs the benchmark without pre-compilation.
     */
    @Test
    fun historyJourneyNoCompilation() = benchmark(CompilationMode.None())

    /**
     * Benchmark test to measure performance with partial compilation and baseline profiles enabled.
     * This test ensures that the app utilizes baseline profiles, and only the methods defined in the baseline profiles
     * are compiled to improve performance.
     */
    @Test
    fun historyJourneyPartialWithBaselineProfiles() =
        benchmark(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    /**
     * Benchmark test to measure performance with partial compilation but baseline profiles disabled.
     * This test runs with partial compilation, but does not make use of baseline profiles to speed up the compilation process.
     * It also performs 3 warmup iterations before measuring the performance to ensure the app is in a steady state.
     */
    @Test
    fun historyJourneyPartialCompilation() = benchmark(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Disable,
            warmupIterations = 3
        )
    )

    /**
     * Helper function to simulate navigating and interacting with the "History" section of the app.
     * This function clicks on the "History" tab, waits for the history section to load,
     * scrolls through the list of history items, clicks a random item, then navigates back.
     * It also interacts with other elements like the top action bar and calendar.
     */
    private fun MacrobenchmarkScope.historyJourney() {
        device.wait(Until.hasObject(By.res("History")), 2000)
        val historyBottomBar = device.getObject(By.res("History"))
        historyBottomBar.click()

        device.waitForIdle()

        device.wait(Until.hasObject(By.res("historyWorkouts")), 1000)
        val lazyColumn = device.getObject(By.res("historyWorkouts"))
        lazyColumn.fling(Direction.UP)
        lazyColumn.fling(Direction.DOWN)

        lazyColumn.children.random().click()
    }

    /**
     * This test assumes you are running on api 34 and above to make use of the saving state between
     * each test. You also need to be logged in beforehand with an account that has some previous
     * workouts
     */
    @SuppressLint("NewApi")
    @OptIn(ExperimentalMetricApi::class)
    private fun benchmark(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(
                StartupTimingMetric(),
                FrameTimingMetric()
            ),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 5,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
                historyJourney()
            }
        )
    }

}

/**
 * Clears the application data for the package specified in the [MacrobenchmarkScope].
 * @param scope The [MacrobenchmarkScope] providing information about the benchmark,
 * including the package name of the app under test.
 */
fun UiDevice.clearData(scope: MacrobenchmarkScope) {
    val command = "pm clear ${scope.packageName}"
    executeShellCommand(command)
}

/**
 * Wrapper for findObject so that we don't get NullPointerException
 */
fun UiDevice.getObject(selector: BySelector): UiObject2 =
    findObject(selector) ?: error("Object not found for: $selector")