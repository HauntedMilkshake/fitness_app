package com.example.app.cases

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import bg.zahov.app.MainActivity
import com.example.app.helpers.MainActivityRobot
import org.junit.Rule
import org.junit.Test

class MainActivityE2EWorkoutTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var robot: MainActivityRobot

    /**
     * Test the following scenario:
     * User starts from welcome screen, navigates to login, logs in with a dummy account,
     * navigates to start workout, starts an empty work out and ensures key buttons are there
     *
     */
    @Test
    fun testStartWorkout() {
        robot = MainActivityRobot(composeTestRule, composeTestRule.activity.applicationContext)
        robot.clickOnLoginButton()
        robot.inputCredentials()
        robot.clickOnLoginButton()
        robot.checkIfInHomePage()
        robot.navigateToStartWorkout()
        robot.startWorkout()
        robot.verifyWorkout()
    }
}