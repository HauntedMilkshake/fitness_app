package com.example.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
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

/**
 * Benchmark tests for measuring performance of the workout journey feature under different compilation modes.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class FinishWorkoutJourney {

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
     * Runs the benchmark test with partial compilation but without baseline profiles, using warmup iterations.
     */
    @Tes
    @Test
    fun finishWorkoutJourneyPartialCompilation() =
        benchmark(
            CompilationMode.Partial(
                baselineProfileMode = BaselineProfileMode.Disable,
                warmupIterations = 3
            )
        )

    /**
     * Executes the workout journey flow from starting a workout to finishing it.
     *
     * Flow of the test login -> start workout -> add exercise to workout -> add set to exercise -> finish workout
     */
    private fun MacrobenchmarkScope.finishWorkoutJourney() {
        device.wait(Until.hasObject(By.res("Workout")), 2000)
        device.waitForIdle()
        device.getObject(By.res("Workout")).click()
        device.wait(Until.hasObject(By.res("StartEmptyWorkoutButton")), 2000)
        device.getObject(By.res("StartEmptyWorkoutButton")).click()
        device.wait(Until.hasObject(By.res("AddExercise")), 2000)
        device.getObject(By.res("AddExercise")).click()
        device.wait(Until.hasObject(By.res("Exercises")), 2000)
        val exercisesLazyColumn = device.getObject(By.res("Exercises"))
        exercisesLazyColumn.fling(Direction.DOWN)
        exercisesLazyColumn.fling(Direction.UP)
        exercisesLazyColumn.fling(listOf(Direction.UP, Direction.DOWN).random())
        exercisesLazyColumn.children.random().click()
        device.getObject(By.res("Confirm")).click()
        device.wait(Until.hasObject(By.res("AddSet")), 2000)
        repeat(1) {
            device.findObjects(By.res("AddSet")).forEach { it.click() }
        }

        device.findObjects(By.res("WeightInputField")).forEachIndexed { index, field ->
            field.text = (10 * (index + 1)).toString()
        }
        device.findObjects(By.res("RepsInputField")).forEachIndexed { index, field ->
            field.text = (12 - index).toString()
        }
        device.getObject(By.res("FinishWorkout")).click()
        device.wait(Until.hasObject(By.textContains("This is workout")), 1000)
        device.waitForIdle()
    }

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
            iterations = 10,
            setupBlock = {
                device.executeShellCommand("pm clear $packageName")
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()
                login()
                finishWorkoutJourney()
            }
        )
    }

    /**
     * Logs in the user by filling the login form and clicking the login button.
     */
    private fun MacrobenchmarkScope.login() {
        device.getObject(By.text("Login")).click()
        device.getObject(By.res("EmailField")).text = "spas@gmail.com"
        device.getObject(By.res("PasswordField")).text = "123456"
        val loginButton = device.getObject(By.res("LoginButton"))
        loginButton.wait(Until.clickable(loginButton.isClickable), 1000)
        loginButton.click()
    }
}

/**
 * Retrieves a UI object using the specified selector, or throws an error if not found.
 *
 * @param selector The selector used to find the UI object.
 * @return The found [UiObject2] if successful.
 * @throws IllegalStateException If the object is not found.
 */
fun UiDevice.getObject(selector: BySelector): UiObject2 =
    findObject(selector) ?: error("Object not found for: $selector")