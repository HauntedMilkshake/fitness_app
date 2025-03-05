package com.example.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
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

    @Test
    fun historyJourneyNoCompilation() = benchmark(CompilationMode.None())

    @Test
    fun historyJourneyPartialWithBaselineProfiles() =
        benchmark(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    @Test
    fun historyJourneyPartialCompilation() = benchmark(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Disable,
            warmupIterations = 3
        )
    )

    private fun MacrobenchmarkScope.login() {
        device.getObject(By.text("Login")).click()
        device.getObject(By.res("EmailField")).text = "spas@gmail.com"
        device.getObject(By.res("PasswordField")).text = "123456"
        val loginButton = device.getObject(By.res("LoginButton"))
        loginButton.wait(Until.clickable(loginButton.isClickable), 1000)
        loginButton.click()
    }

    private fun MacrobenchmarkScope.historyJourney() {
        device.wait(Until.hasObject(By.res("History")), 1000)
        val historyBottomBar = device.getObject(By.res("History"))
        historyBottomBar.click()

        device.waitForIdle()

        device.wait(Until.hasObject(By.res("historyWorkouts")), 1000)
        val lazyColumn = device.getObject(By.res("historyWorkouts"))
        lazyColumn.fling(Direction.UP)
        lazyColumn.fling(Direction.DOWN)

        lazyColumn.children.random().click()
//        device.getObject(By.res("Workout")).click()

        device.getObject(By.res("HistoryBack")).click()

        device.wait(Until.hasObject(By.res("TopBarAction")), 1000)
        device.getObject(By.res("TopBarAction")).click()

        device.wait(Until.hasObject(By.res("Calendar")), 1000)
        val calendar = device.getObject(By.res("Calendar"))
        calendar.also {
            it.fling(Direction.LEFT)
            it.fling(Direction.RIGHT)
        }
    }

    @Test
    fun generate() {
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            includeInStartupProfile = true
        ) {
            pressHome()
            startActivityAndWait()
            login()
            historyJourney()
        }
    }

    private fun benchmark(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
                login()
                historyJourney()
            }
        )
    }

}

fun UiDevice.getObject(selector: BySelector): UiObject2 =
    findObject(selector) ?: error("Object not found for: $selector")