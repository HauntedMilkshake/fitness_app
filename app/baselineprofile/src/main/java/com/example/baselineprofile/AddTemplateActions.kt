package com.example.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.UiSelector

fun MacrobenchmarkScope.addTemplateJourney() {
    device.findObject(UiSelector().text("Workout")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().description("Add template")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().resourceId("Add name")).setText("Test template")
    device.findObject(UiSelector().text("Add exercise")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().resourceId("Exercises"))
        .getChild(UiSelector().index(0)).click()
    device.findObject(UiSelector().description("confirm")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().text("Test Workout")).exists()
    device.findObject(UiSelector().description("action")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().text("Test template")).exists()
}