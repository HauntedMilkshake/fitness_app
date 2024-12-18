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
data object Exercises

@Serializable
object ExerciseInfo

@Serializable
object Measure

@Serializable
object MeasureInfo