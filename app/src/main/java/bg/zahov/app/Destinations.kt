package bg.zahov.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Destinations {
    @Parcelize
    data object Welcome : Parcelable

    @Parcelize
    data object Login : Parcelable

    @Parcelize
    data object Signup : Parcelable

    @Parcelize
    data object Loading : Parcelable

    @Parcelize
    data object Home : Parcelable

    @Parcelize
    data object Settings : Parcelable

    @Parcelize
    data object EditProfile : Parcelable

    @Parcelize
    data object History : Parcelable

    @Parcelize
    data object StartWorkout : Parcelable

    @Parcelize
    data class AddTemplateWorkout(val workoutId: String? = null) : Parcelable

    @Parcelize
    data object Workout : Parcelable

    @Parcelize
    data class Exercises(val state: String? = null) : Parcelable

    @Parcelize
    data object ExerciseInfo : Parcelable

    @Parcelize
    object ExerciseAdd : Parcelable

    @Parcelize
    data object Measure : Parcelable

    @Parcelize
    data object MeasureInfo : Parcelable

    @Parcelize
    data object HistoryInfo : Parcelable

    @Parcelize
    data object Calendar : Parcelable
}