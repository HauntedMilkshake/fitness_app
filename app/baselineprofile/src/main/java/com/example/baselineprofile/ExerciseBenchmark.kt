package com.example.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Measures the startup performance without any compilation optimizations.
     */
    @Test
    fun exerciseJourneyNoCompilation() = benchmark(CompilationMode.None())

    /**
     * Measures the startup performance with partial compilation using baseline profiles.
     */
    @Test
    fun exerciseJourneyPartialWithBaselineProfiles() =
        benchmark(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    /**
     * Runs the benchmark test with the specified compilation mode.
     *
     * @param compilationMode The mode in which the app should be compiled before benchmarking.
     * @throws Exception if the target application ID is not provided.
     */
    private fun benchmark(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            iterations = 20,
            setupBlock = {
                device.executeShellCommand("pm clear $packageName")
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
                exerciseJourney()
            })
    }
}