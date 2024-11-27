package com.example.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithTag
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.ui.exercise.info.ExerciseHistoryInfo
import bg.zahov.app.ui.exercise.info.ExerciseInfoContent
import com.github.mikephil.charting.data.Entry
import org.junit.Rule
import org.junit.Test

class ExerciseInfoTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun exerciseInfoContentDisplaysCorrectly() {
        val testCharts = listOf(
            LineChartData(
                text = "Weight Progress",
                maxValue = 100f,
                minValue = 50f,
                suffix = MeasurementType.Weight,
                list = listOf(
                    Entry(1f, 60f),
                    Entry(2f, 65f),
                    Entry(3f, 70f)
                )
            ),
            LineChartData(
                text = "Running Distance",
                maxValue = 20f,
                minValue = 5f,
                suffix = MeasurementType.Weight,
                list = listOf(
                    Entry(1f, 6f),
                    Entry(2f, 8f),
                    Entry(3f, 12f)
                )
            )
        )
        val testHistory = listOf(
            ExerciseHistoryInfo(
                workoutName = "Bench Press",
                lastPerformed = "Yesterday",
                setsPerformed = "5 sets",
                oneRepMaxes = "100 kg"
            ),
            ExerciseHistoryInfo(
                workoutName = "Deadlift",
                lastPerformed = "2 days ago",
                setsPerformed = "4 sets",
                oneRepMaxes = "120 kg"
            )
        )
        composeTestRule.setContent {
            ExerciseInfoContent(charts = testCharts, history = testHistory)
        }

        // Validate charts are displayed
        composeTestRule.onNodeWithText("Weight Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("Running Distance").assertIsDisplayed()

        // Validate history data is displayed
        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yesterday").assertIsDisplayed()
        composeTestRule.onNodeWithText("Deadlift").assertIsDisplayed()
        composeTestRule.onNodeWithText("4 sets").assertIsDisplayed()

        // Simulate click and validate behavior
        composeTestRule.onNodeWithText("Weight Progress").performClick()
        composeTestRule.onNodeWithText("Weight Progress").assertIsDisplayed() // Example: Add post-click assertions
    }
}
