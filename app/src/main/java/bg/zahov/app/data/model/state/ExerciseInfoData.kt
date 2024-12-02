package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Sets
import java.time.LocalDateTime

/**
 * A data class representing the exercise history data and associated statistics.
 *
 * @property loading Indicates whether the data is currently being loaded.
 * @property exerciseHistory A list of exercise history information, containing details of individual exercises.
 * @property oneRepMaxEst The line chart data for the estimated one-rep max.
 * @property maxVolume The line chart data for the maximum volume lifted.
 * @property maxRep The line chart data for the maximum number of repetitions performed.
 */
data class ExerciseHistoryData(
    val loading: Boolean = true,
    val exerciseHistory: List<ExerciseHistoryInfo> = listOf(),
    val oneRepMaxEst: LineChartData = LineChartData(
        text = "One Rep Max Estimate",
        suffix = MeasurementType.Weight
    ),
    val maxVolume: LineChartData = LineChartData(
        text = "Max Volume",
        suffix = MeasurementType.Weight
    ),
    val maxRep: LineChartData = LineChartData(
        text = "Max Rep",
        suffix = MeasurementType.Reps
    ),
)

/**
 * A data class representing detailed information about a specific exercise's history.
 *
 * @property workoutName The name of the workout associated with the exercise.
 * @property lastPerformed A string indicating the last time the exercise was performed.
 * @property sets A list of sets performed for the exercise.
 * @property setsPerformed A string summarizing the sets performed.
 * @property oneRepMaxes A string representation of one-rep maxes for the exercise.
 * @property date The date and time the exercise history entry was created or logged.
 */
data class ExerciseHistoryInfo(
    val workoutName: String = "",
    val lastPerformed: String = "",
    val sets: List<Sets> = listOf(),
    val setsPerformed: String = "",
    val oneRepMaxes: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
)