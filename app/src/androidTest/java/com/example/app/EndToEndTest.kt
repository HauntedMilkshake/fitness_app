package com.example.app

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import bg.zahov.app.MainActivity
import bg.zahov.fitness.app.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: TestNavHostController
    private lateinit var context: Context

    @Before
    fun setupAppNavHost() {
        context = composeTestRule.activity.applicationContext
        navController = TestNavHostController(context)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
    }

    @Test
    fun workoutFeatureJourney() {
//      Data for creating and login in user
        val email = "test@test.test"
        val user = "test"
        val password = "123456"
//      Name for test exercise
        val exerciseName = "Text Exercise"

//      Testing auth functionalities
        composeTestRule.onNodeWithText("Sign up").performClick()
        composeTestRule.onNodeWithText("Already have an account? Log in here!").performClick()
        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Password").performTextInput(password)
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.show_password)).performClick()
        composeTestRule.onNodeWithTag("Login Button").performClick()
//      Testing Home Screen
//      5 sec loading period
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(user)[0].isDisplayed()
        }
//      Setting Screen
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.top_bar_action)).performClick()
        composeTestRule.onAllNodesWithText("Settings")[0].isDisplayed()
        composeTestRule.onNodeWithText("Language").performClick()
        composeTestRule.onNodeWithTag("RadioSettings").assertIsDisplayed()
        Espresso.pressBack()
//      Edit Profile Screen
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.top_bar_action)).performClick()
        composeTestRule.onAllNodesWithText("Edit profile")[0].assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back_button)).performClick()
//      Back to settings
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back_button)).performClick()
        composeTestRule.onNodeWithTag("Exercise").performClick()
//      Exercise Screen
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.add_exercise)).performClick()
        composeTestRule.onNodeWithText("Category:").performClick()
        composeTestRule.onNodeWithText("Barbell").performClick()
        Espresso.pressBack()
        composeTestRule.onNodeWithText("Body part:").performClick()
        composeTestRule.onNodeWithText("Core").performClick()
        Espresso.pressBack()
        composeTestRule.onNodeWithText("Add name").performTextInput(exerciseName)
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.confirm)).performClick()
//      Going to workout
        composeTestRule.onNodeWithTag("Workout").performClick()
        composeTestRule.onNodeWithText("start an empty workout").performClick()
        composeTestRule.onNodeWithText("Add note").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add exercise").performClick()
        composeTestRule.onNodeWithText(exerciseName).performClick()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.confirm)).performClick()
        composeTestRule.onNodeWithText(exerciseName).assertIsDisplayed()
        composeTestRule.onNodeWithText("add set").performClick()
        composeTestRule.onAllNodesWithTag("Reps value")[0].performTextInput("6")
        composeTestRule.onAllNodesWithTag("Weight value")[0].performTextInput("6")
        composeTestRule.onNodeWithText("Finish").performClick()
        Espresso.pressBack()
//      History Screen
        composeTestRule.onNodeWithTag("History").performClick()
//      Testing calendar functions
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.top_bar_action)).performClick()
        val calendar = Calendar.getInstance()
        var month = SimpleDateFormat("MMMM", Locale.ENGLISH)
            .format(calendar.time)
            .uppercase(Locale.ENGLISH)
        composeTestRule.onNodeWithText(month).assertIsDisplayed()
        composeTestRule.onNodeWithText(month).performTouchInput { swipeRight() }
        calendar.add(Calendar.MONTH, -1)
        month = SimpleDateFormat("MMMM", Locale.ENGLISH)
            .format(calendar.time)
            .uppercase(Locale.ENGLISH)
        composeTestRule.onNodeWithText(month).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back_button)).performClick()
    }
}