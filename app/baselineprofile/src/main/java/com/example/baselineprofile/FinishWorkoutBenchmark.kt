package com.example.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark tests for measuring performance of the workout journey feature under different compilation modes.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class FinishWorkoutBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    /**
     * Runs the benchmark test without any compilation optimization.
     */
    @Test
    fun finishWorkoutJourneyNoCompilation() =
        benchmark(CompilationMode.None())

    /**
     * Runs the benchmark test with partial compilation and baseline profiles enabled.
     */
    @Test
    fun finishWorkoutJourneyPartialWithBaselineProfiles() =
        benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    /**
     * Benchmarks the provided compilation mode by measuring the startup time of the workout journey feature.
     *
     * @param compilationMode The compilation mode to be applied for the test.
     */
    private fun benchmark(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 20,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
                finishWorkoutJourney()
            }
        )
    }
}
