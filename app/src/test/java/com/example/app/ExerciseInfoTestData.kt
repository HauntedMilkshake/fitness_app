package bg.zahov.app.ui.exercise.info

import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.MeasurementType
import com.github.mikephil.charting.data.Entry

object TestData {
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
    val testOneRepMaxEst =
        LineChartData(
            text = "One Rep Max Estimate",
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
        text = "Max Volume",
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
        text = "Max Rep",
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