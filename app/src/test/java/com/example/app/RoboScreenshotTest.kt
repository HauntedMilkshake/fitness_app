package com.example.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import bg.zahov.app.MainActivity
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RoboScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        composeTestRule.waitForIdle()
    }

    @Test
    fun screenshotWelcomeScreen() {
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun screenshotLoginScreen() {
        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun screenshotSignupScreen() {
        composeTestRule.onNodeWithText("Sign up").performClick()
        composeTestRule.onNodeWithText("Username").captureRoboImage()
    }
}