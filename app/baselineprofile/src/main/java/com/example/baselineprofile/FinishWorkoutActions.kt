package com.example.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until

private const val TIMEOUT: Long = 2000

/**
 * Executes the workout journey flow from starting a workout to finishing it.
 *
 * Flow of the test login -> start workout -> add exercise to workout -> add set to exercise -> finish workout
 */
fun MacrobenchmarkScope.finishWorkoutJourney() {
    device.getObject(By.res("Workout")).click()
    device.getObject(By.res("StartEmptyWorkoutButton")).click()
    device.getObject(By.res("AddExercise")).click()

    device.wait(Until.hasObject(By.res("Exercises")), TIMEOUT)
    val exercisesLazyColumn = device.getObject(By.res("Exercises"))
    exercisesLazyColumn.fling(Direction.DOWN)
    exercisesLazyColumn.fling(Direction.UP)

    exercisesLazyColumn.children.random().click()
    device.getObject(By.res("Confirm")).click()
    device.wait(Until.hasObject(By.res("AddSet")), TIMEOUT)

    device.findObjects(By.res("AddSet")).forEach { it.click() }

    device.findObjects(By.res("WeightInputField")).forEachIndexed { index, field ->
        field.text = (10 * (index + 1)).toString()
    }
    device.findObjects(By.res("RepsInputField")).forEachIndexed { index, field ->
        field.text = (12 - index).toString()
    }
    device.getObject(By.res("FinishWorkout")).click()
}