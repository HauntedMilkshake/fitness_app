package bg.zahov.app

import kotlinx.serialization.Serializable

@Serializable
data object Welcome

@Serializable
data object Login

@Serializable
data object Signup

@Serializable
data object Loading

@Serializable
data object Home

@Serializable
data object Settings

@Serializable
data object EditProfile

@Serializable
data object History

@Serializable
data object StartWorkout

@Serializable
data class AddTemplateWorkout(val workoutId: String? = null)

@Serializable
data object Workout

@Serializable
data class Exercises(val state: String? = null)

@Serializable
data object ExerciseInfo

@Serializable
object ExerciseAdd

@Serializable
data object Measure

@Serializable
data object MeasureInfo

@Serializable
data object HistoryInfo

@Serializable
data object Calendar

@Serializable
data object Rest

@Serializable
data object WorkoutFinish