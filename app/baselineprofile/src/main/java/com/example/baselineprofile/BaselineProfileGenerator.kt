package com.example.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(
            packageName = PACKAGE_NAME,
            includeInStartupProfile = true
        ) {
            pressHome()
            startActivityAndWait()
        }
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

const val PACKAGE_NAME = "bg.zahov.fitness.app.mock"