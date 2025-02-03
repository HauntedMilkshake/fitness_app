package com.example.app

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import bg.zahov.app.App
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            App(navController = navController)
        }
    }

    @Test
    fun endToEndTest() {
        //Data for creating and login in user
        val email = "test@test.test"
        val user = "test"
        val password = "123456"

        //Testing sign up screen functionalities
        registerTest(email = email, user = user, pass = password)

        //Testing Home Screen
        //3 sec loading period
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText(user)[0].isDisplayed()
        }
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onNodeWithTag("Title").equals("Home")
        }
//        composeTestRule.onNodeWithTag("Top Bar Action").assertExists()
//        composeTestRule.onNodeWithTag("Top Bar Action").performClick()
//
//        //Testing Setting Screen
//        composeTestRule.onNodeWithTag("Title").assertTextEquals("Settings")
//        composeTestRule.onNodeWithText("Language").performClick()
//        composeTestRule.onNodeWithTag("RadioButtons").assertIsDisplayed()
//        Espresso.pressBack()
//        composeTestRule.onNodeWithTag("Top Bar Action").performClick()
//
//        //Testing Edit Profile
//        composeTestRule.onNodeWithTag("Back").performClick()
    }

    //Performs all the necessary move to log into the test user
    fun loginTest(email: String, pass: String) {
        composeTestRule.onNodeWithText("Sign up").performClick()
        composeTestRule.onNodeWithText("Already have an account? Log in here!").performClick()

        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Password").performTextInput(pass)
        composeTestRule.onNodeWithContentDescription("show password").performClick()
        composeTestRule.onNodeWithTag("Login").performClick()
    }

    //Performs all the necessary move to register a test user
    private fun registerTest(email: String = "defualt", user: String = "de@fa.ult", pass: String = "123456") {
        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.onNodeWithText("No account? Sign up here!").performClick()

        composeTestRule.onNodeWithText("Username").performTextInput(user)
        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Password").performTextInput(pass)
        composeTestRule.onNodeWithText("Confirm").performTextInput(pass)
        composeTestRule.onAllNodesWithContentDescription("show password")[0].performClick()
        composeTestRule.onNodeWithTag("Sign Up").performClick()
    }
}