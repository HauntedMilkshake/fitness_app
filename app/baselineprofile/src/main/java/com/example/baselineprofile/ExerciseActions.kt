package com.example.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.UiSelector

fun MacrobenchmarkScope.exerciseJourney() {
    device.findObject(UiSelector().text("Exercise")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().description("Add exercise")).clickAndWaitForNewWindow()
    device.findObject(UiSelector().text("Category:")).click()
    device.findObject(UiSelector().text("Barbell")).click()
    device.pressBack()
    device.findObject(UiSelector().text("Body part:")).click()
    device.findObject(UiSelector().text("Core")).click()
    device.pressBack()
    device.findObject(UiSelector().resourceId("Add Name")).setText("Deadlift")
    device.findObject(UiSelector().description("confirm")).click()
}