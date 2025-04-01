package com.example.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTemplateBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun exerciseJourneyNoCompilation() = benchmark(CompilationMode.None())

    @Test
    fun exerciseJourneyPartialWithBaselineProfiles() =
        benchmark(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    private fun benchmark(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = "bg.zahov.fitness.app.mock",
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            iterations = 20,
            setupBlock = {
                device.executeShellCommand("pm clear $packageName")
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
                addTemplateJourney()
            })
    }
}
