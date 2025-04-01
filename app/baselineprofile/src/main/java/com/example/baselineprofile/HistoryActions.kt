package com.example.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * Helper function to simulate navigating and interacting with the "History" section of the app.
 * This function clicks on the "History" tab, waits for the history section to load,
 * scrolls through the list of history items, clicks a random item, then navigates back.
 * It also interacts with other elements like the top action bar and calendar.
 */
fun MacrobenchmarkScope.historyJourney() {
    device.wait(Until.hasObject(By.res("History")), 2000)
    val historyBottomBar = device.getObject(By.res("History"))
    historyBottomBar.click()

    device.waitForIdle()

    device.wait(Until.hasObject(By.res("historyWorkouts")), 1000)
    val lazyColumn = device.getObject(By.res("historyWorkouts"))
    lazyColumn.fling(Direction.UP)
    lazyColumn.fling(Direction.DOWN)

    lazyColumn.children.random().click()
}


/**
 * Helper function to simulate user login in the app.
 * The login function clicks on the "Login" button, fills in the login form with a test email and password,
 * waits for the login button to become clickable, and then clicks the button to log in.
 * This function is used as a precondition for tests that require a logged-in state.
 */
fun MacrobenchmarkScope.login() {
    device.getObject(By.text("Login")).click()
    device.getObject(By.res("EmailField")).text = "spas@gmail.com"
    device.getObject(By.res("PasswordField")).text = "123456"
    val loginButton = device.getObject(By.res("LoginButton"))
    loginButton.wait(Until.clickable(loginButton.isClickable), 1000)
    loginButton.click()
}

fun UiDevice.getObject(selector: BySelector): UiObject2 =
    findObject(selector) ?: error("Object not found for: $selector")
