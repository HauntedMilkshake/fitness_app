package bg.zahov.app.data.provider.model

/**
 * Represents detailed information about a specific exercise within a workout.
 *
 * @property exerciseName The name of the exercise.
 * @property sets A list of sets performed for the exercise, represented as strings.
 * @property oneRepMaxes A list of one-rep maxes for the exercise, represented as strings.
 */
data class ExerciseDetails(
    val exerciseName: String,
    val sets: List<String>,
    val oneRepMaxes: List<String>,
)

/**
 * Represents the detailed information of a workout history entry.
 *
 * @property id The unique identifier of the workout.
 * @property workoutName The name of the workout.
 * @property workoutDate The date when the workout was performed.
 * @property duration The duration of the workout, represented as a string.
 * @property volume The total volume lifted during the workout, represented as a string.
 * @property prs The personal records achieved during the workout, represented as a string.
 * @property exercisesInfo A list of detailed information for each exercise in the workout.
 * @property isDeleted indicates whether the workout has been deleted
 */
data class HistoryInfoWorkout(
    val id: String = "",
    val workoutName: String = "",
    val workoutDate: String = "",
    val duration: String = "",
    val volume: String = "",
    val prs: String = "",
    val exercisesInfo: List<ExerciseDetails> = listOf(),
    val isDeleted: Boolean = false,
)
