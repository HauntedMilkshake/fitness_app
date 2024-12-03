package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Sets
import bg.zahov.fitness.app.R
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
        textId = R.string.one_rep_max_title_text,
        suffix = MeasurementType.Weight
    ),
    val maxVolume: LineChartData = LineChartData(
        textId = R.string.max_volume,
        suffix = MeasurementType.Weight
    ),
    val maxRep: LineChartData = LineChartData(
        textId = R.string.max_weight,
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
    val oneRepMaxes: List<String> = listOf(),
    val date: LocalDateTime = LocalDateTime.now(),
)

/**
 * Enum class representing keys for shared elements in the ExerciseChartInfo UI.
 */
enum class SharedElementKey(private val keySuffix: String) {
    BOUND("-bound"),
    TEXT("-text"),
    VALUE("-value");

    /**
     * Generates the full key by appending the suffix to a given base key.
     * If the base key is null or empty, a fallback key is generated internally.
     *
     * @param baseKey The base key to which the suffix will be appended.
     * @return The full key for the shared element.
     */
    fun generateKey(baseKey: String?): String {
        val validatedBaseKey = baseKey.takeUnless { it.isNullOrEmpty() } ?: "defaultKey"
        return "$validatedBaseKey$keySuffix"
    }
}