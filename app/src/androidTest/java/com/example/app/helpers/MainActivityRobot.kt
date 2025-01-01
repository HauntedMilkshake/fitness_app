package com.example.app.helpers

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import bg.zahov.fitness.app.R


class MainActivityRobot(
    private val composeTestRule: ComposeTestRule,
    private val context: Context,
) {
    fun clickOnLoginButton() {
        composeTestRule.onNodeWithTag(context.getString(R.string.login_button_test_tag))
            .performClick()
    }

    fun inputCredentials(email: String = "test@gmail.com", password: String = "123456") {
        composeTestRule.onNodeWithText(context.getString(R.string.email_text_field_hint))
            .performTextInput(email)
        composeTestRule.onNodeWithText(context.getString(R.string.password_text_field_hint))
            .performTextInput(password)

        //to enable the login button
        composeTestRule.mainClock.advanceTimeBy(500)
    }

    fun checkIfInHomePage() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
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
}