package com.example.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UIAutomationExercise {

    @get:Rule
    val rule = BaselineProfileRule()

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun exerciseJourneyNoCompilation() = benchmark(CompilationMode.None())

    @Test
    fun exerciseJourneyPartialWithBaselineProfiles() =
        benchmark(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    @Test
    fun exerciseJourneyPartialCompilation() = benchmark(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Disable, warmupIterations = 3
        )
    )

    private fun benchmark(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = "bg.zahov.fitness.app.mock",
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
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

fun MacrobenchmarkScope.exerciseJourney() {
    device.findObject(UiSelector().text("Exercise")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().description("Add exercise")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().text("Category:")).click()
    device.findObject(UiSelector().text("Barbell")).click()
    device.pressBack()
    device.findObject(UiSelector().text("Body part:")).click()
    device.findObject(UiSelector().text("Core")).click()
    device.pressBack()
    device.findObject(UiSelector().resourceId("Add Name")).setText("Deadlift")
    device.findObject(UiSelector().description("confirm")).click()
}
