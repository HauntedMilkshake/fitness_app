package com.example.app

import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.state.ExerciseHistoryInfo
import com.github.mikephil.charting.data.Entry
import bg.zahov.fitness.app.R

object ExerciseInfoTestData {
    val testHistory = listOf(
        ExerciseHistoryInfo(
            workoutName = "Bench Press",
            lastPerformed = "Yesterday",
            setsPerformed = "5 sets",
            oneRepMaxes = listOf()//"100 kg"
        ),
        ExerciseHistoryInfo(
            workoutName = "Deadlift",
            lastPerformed = "2 days ago",
            setsPerformed = "4 sets",
            oneRepMaxes = listOf()//"120 kg"
        )
    )
    val testOneRepMaxEst =
        LineChartData(
            textId = R.string.one_rep_max_text,
            maxValue = 100f,
            minValue = 50f,
            suffix = MeasurementType.Weight,
            list = listOf(
                Entry(1f, 60f),
                Entry(2f, 65f),
                Entry(3f, 70f),
                Entry(4f, 75f),
                Entry(5f, 80f)
            )
        )
    val testMaxVolume = LineChartData(
        textId = R.string.max_volume,
        maxValue = 20f,
        minValue = 5f,
        suffix = MeasurementType.Weight,
        list = listOf(
            Entry(1f, 6f),
            Entry(2f, 8f),
            Entry(3f, 12f),
            Entry(4f, 15f),
            Entry(5f, 18f)
        )
    )
    val testMaxRep = LineChartData(
        textId = R.string.max_weight,
        maxValue = 500f,
        minValue = 100f,
        suffix = MeasurementType.Weight,
        list = listOf(
            Entry(1f, 150f),
            Entry(2f, 200f),
            Entry(3f, 300f),
            Entry(4f, 400f),
            Entry(5f, 450f)
        )
    )
}