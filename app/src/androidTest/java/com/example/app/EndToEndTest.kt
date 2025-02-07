package com.example.app

import android.content.Context
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
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
    fun endToEndTest() {
        //Data for creating and login in user
        val email = "test@test.test"
        val user = "test"
        val password = "123456"
        //Name for test exercise
        val exerciseName = "Text Exercise"

        //Testing auth functionalities
//        registerTest(email = email, user = user, pass = password)
//        Espresso.pressBack()
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithText(context.getString(R.string.register)).isDisplayed()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.register)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.already_have_account_text))
            .performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.email_text_field_hint))
            .performTextInput(email)
        composeTestRule.onNodeWithText(context.getString(R.string.password_text_field_hint))
            .performTextInput(password)
        composeTestRule.onNodeWithContentDescription("show password").performClick()
        composeTestRule.onNodeWithTag(context.getString(R.string.login)).performClick()

        //Testing Home Screen
        //5 sec loading period
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(user)[0].isDisplayed()
        }
//        Setting Screen
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithTag("Title").isDisplayed()
        }
        composeTestRule.onNodeWithTag("Action").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText(context.getString(R.string.settings_text))[0].isDisplayed()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.language_text)).performClick()
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithTag("RadioSettings").isDisplayed()
        }
        Espresso.pressBack()

        //Edit Profile Screen
        composeTestRule.onNodeWithTag("Action").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText(context.getString(R.string.edit_profile_text))[0].isDisplayed()
        }
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithTag("Back").isDisplayed()
        }
        composeTestRule.onNodeWithTag("Back").performClick()
//        Back to settings
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithTag("Back").isDisplayed()
        }
        composeTestRule.onNodeWithTag("Back").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(context.getString(R.string.exercise)).performClick()
//        Exercise Screen
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("Add").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(context.getString(R.string.category)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(context.getString(R.string.barbell_category_text))
            .performClick()
        Espresso.pressBack()
        composeTestRule.onNodeWithText(context.getString(R.string.body_part)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Back").performClick()
        Espresso.pressBack()
        composeTestRule.onNodeWithText(context.getString(R.string.add_name_hint))
            .performTextInput(exerciseName)
        composeTestRule.onNodeWithTag(context.getString(R.string.confirm)).performClick()
//        Going to workout
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(context.getString(R.string.workout)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(context.getString(R.string.start_empty_workout_text))
            .isDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.start_empty_workout_text))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.add_note)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.add_exercise)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithText(exerciseName).isDisplayed()
        }
        composeTestRule.onNodeWithText(exerciseName).performClick()
        composeTestRule.onNodeWithTag(context.getString(R.string.language_text)).performClick()
        composeTestRule.onNodeWithText(exerciseName).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.add_set)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithTag("Value")[0].performTextInput("6")
        composeTestRule.onAllNodesWithTag("Value")[1].performTextInput("6")
        composeTestRule.onNodeWithTag("Action").performClick()
        Espresso.pressBack()

        //        History Screen
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(context.getString(R.string.history)).performClick()
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithTag("Title").isDisplayed()
        }

//        Testing calendar functions
        composeTestRule.onNodeWithTag("Action").performClick()
        val calendar = Calendar.getInstance()
        var month = SimpleDateFormat("MMMM", Locale.ENGLISH)
            .format(calendar.time)
            .uppercase(Locale.ENGLISH)
        Log.d("month", month)
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithText(month).isDisplayed()
        }
        composeTestRule.onNodeWithText(month).performTouchInput { swipeRight() }

        calendar.add(Calendar.MONTH, -1)
        month = SimpleDateFormat("MMMM", Locale.ENGLISH)
            .format(calendar.time)
            .uppercase(Locale.ENGLISH)
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithText(month).isDisplayed()
        }

        composeTestRule.onNodeWithTag("Back").performClick()
//        Back to history

    }

    //Performs all the necessary move to log into the test user
    private fun loginTest(email: String, pass: String) {
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithText(context.getString(R.string.register)).isDisplayed()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.register)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.already_have_account_text))
            .performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.email_text_field_hint))
            .performTextInput(email)
        composeTestRule.onNodeWithText(context.getString(R.string.password_text_field_hint))
            .performTextInput(pass)
        composeTestRule.onNodeWithContentDescription("show password").performClick()
        composeTestRule.onNodeWithTag(context.getString(R.string.login)).performClick()
    }

    //Performs all the necessary move to register a test user
    private fun registerTest(
        email: String = "defualt",
        user: String = "de@fa.ult",
        pass: String = "123456"
    ) {
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithText(context.getString(R.string.login)).isDisplayed()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.login)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.no_account_text)).performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.username_text))
            .performTextInput(user)
        composeTestRule.onNodeWithText(context.getString(R.string.email_text_field_hint))
            .performTextInput(email)
        composeTestRule.onNodeWithText(context.getString(R.string.password_text_field_hint))
            .performTextInput(pass)
        composeTestRule.onNodeWithText(context.getString(R.string.confirm)).performTextInput(pass)
        composeTestRule.onAllNodesWithContentDescription("show password")[0].performClick()
        composeTestRule.onNodeWithTag(context.getString(R.string.register)).performClick()
    }
}