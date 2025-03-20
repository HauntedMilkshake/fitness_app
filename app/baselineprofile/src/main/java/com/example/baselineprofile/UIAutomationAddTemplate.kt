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
class UIAutomationAddTemplate {

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

    private fun MacrobenchmarkScope.addTemplateJourney() {
        device.findObject(UiSelector().text("Workout")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().description("Add template")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().resourceId("Add name")).setText("Test template")
        device.findObject(UiSelector().text("Add exercise")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().resourceId("Exercises"))
            .getChild(UiSelector().index(0)).click()
        device.findObject(UiSelector().description("confirm")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().text("Test Workout")).exists()
        device.findObject(UiSelector().description("action")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().text("Test template")).exists()
    }

    fun generate() {
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            includeInStartupProfile = true,
        ) {
            device.executeShellCommand("pm clear $packageName")
            pressHome()
            startActivityAndWait()
            addTemplateJourney()
        }
    }

    private fun benchmark(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            iterations = 3,
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
