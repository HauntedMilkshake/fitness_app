package com.example.app.cases

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import bg.zahov.app.MainActivity
import bg.zahov.fitness.app.R
import org.junit.Rule
import org.junit.Test
import kotlin.math.log

class MainActivityE2EWorkoutTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private lateinit var context: Context

    fun goToLoginScreen() {
        composeTestRule.onNodeWithTag("to_login_screen")
            .performClick()
    }

    fun login() {
        composeTestRule.onNodeWithTag("to_home_screen").performClick()
    }

    fun inputCredentials(email: String = "test@gmail.com", password: String = "123456") {
        composeTestRule.onNodeWithText(context.getString(R.string.email_text_field_hint))
            .performTextInput(email)
        composeTestRule.onNodeWithText(context.getString(R.string.password_text_field_hint))
            .performTextInput(password)
    }

    fun checkIfInHomePage() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onNodeWithText(context.getString(R.string.dashboard)).isDisplayed()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.dashboard)).assertIsDisplayed()
    }

    fun navigateToStartWorkout() {
        composeTestRule.onNodeWithText(context.getString(R.string.workout)).performClick()
    }

    fun startWorkout() {
        composeTestRule.onNodeWithText(context.getString(R.string.start_empty_workout_text))
            .performClick()
    }

    fun verifyWorkout() {
        composeTestRule.onNodeWithText(context.getString(R.string.add_note)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.add_exercise)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).assertIsDisplayed()
    }

    /**
     * Test the following scenario:
     * User starts from welcome screen, navigates to login, logs in with a dummy account,
     * navigates to start workout, starts an empty work out and ensures key buttons are there
     *
     */
    @Test
    fun testStartWorkout() {
        context = composeTestRule.activity.applicationContext

        goToLoginScreen()
        inputCredentials()
        login()
        checkIfInHomePage()
        navigateToStartWorkout()
        startWorkout()
        verifyWorkout()
    }
}
