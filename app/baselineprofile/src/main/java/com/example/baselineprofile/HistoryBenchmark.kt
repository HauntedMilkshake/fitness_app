package com.example.baselineprofile

import android.annotation.SuppressLint
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HistoryBenchmark {

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
     * This test assumes you are running on api 34 and above to make use of the saving state between
     * each test. You also need to be logged in beforehand with an account that has some previous
     * workouts
     */
    @SuppressLint("NewApi")
    private fun benchmark(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = "bg.zahov.fitness.app.mock",
            metrics = listOf(StartupTimingMetric(), ),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 20,
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