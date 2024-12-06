package com.example.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import bg.zahov.app.ui.exercise.info.ExerciseInfoContent
import org.junit.Rule
import org.junit.Test

class ExerciseInfoTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun exerciseInfoContentDisplaysCorrectly() {
        composeTestRule.setContent {
            ExerciseInfoContent(
                history = ExerciseInfoTestData.testHistory,
                oneRepMaxEst = ExerciseInfoTestData.testOneRepMaxEst,
                maxVolume = ExerciseInfoTestData.testMaxVolume,
                maxRep = ExerciseInfoTestData.testMaxRep
            )
        }

        composeTestRule.onNodeWithText("Max Rep").assertIsDisplayed()
        composeTestRule.onNodeWithText("One Rep Max Estimate").assertIsDisplayed()

        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yesterday").assertIsDisplayed()
        composeTestRule.onNodeWithText("Deadlift").assertIsDisplayed()
        composeTestRule.onNodeWithText("4 sets").assertIsDisplayed()

        composeTestRule.onNodeWithText("Max Volume").performClick()
        composeTestRule.onNodeWithText("PERSONAL RECORDS:").assertIsDisplayed()
    }
}