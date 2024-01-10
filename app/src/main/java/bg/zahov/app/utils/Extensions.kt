package bg.zahov.app.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.core.content.ContextCompat
import bg.zahov.app.data.Filter
import bg.zahov.app.data.SelectableExercise
import bg.zahov.app.backend.Exercise
import bg.zahov.app.backend.Sets
import bg.zahov.app.backend.Settings
import bg.zahov.app.backend.User
import bg.zahov.app.backend.Workout
import bg.zahov.fitness.app.R
import io.realm.kotlin.Realm
import io.realm.kotlin.types.RealmList

fun String.isAValidEmail() = Regex("^\\S+@\\S+\\.\\S+$").matches(this)
fun User.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "username" to username,
        "numberOfWorkouts" to numberOfWorkouts
    )
}

fun User.equalsTo(otherUser: User): Boolean {
    return this.username == otherUser.username && this.numberOfWorkouts == otherUser.numberOfWorkouts
}

fun Settings.equalTo(newSettings: Settings): Boolean {
    return this.language == newSettings.language &&
            this.units == newSettings.units &&
            this.soundEffects == newSettings.soundEffects &&
            this.theme == newSettings.theme &&
            this.restTimer == newSettings.restTimer &&
            this.vibration == newSettings.vibration &&
            this.soundSettings == newSettings.soundSettings &&
            this.updateTemplate == newSettings.updateTemplate &&
            this.fit == newSettings.fit &&
            this.automaticSync == newSettings.automaticSync
}

fun Workout.equalsTo(newWorkout: Workout): Boolean {

    this.isTemplate?.let {
        return exerciseIds.toList() == newWorkout.exerciseIds.toList()
    }

    return this.duration == newWorkout.duration &&
            this.totalVolume == newWorkout.totalVolume &&
            this.numberOfPrs == newWorkout.numberOfPrs &&
            this.workoutName == newWorkout.workoutName &&
            this.date == newWorkout.date &&
            this.count == newWorkout.count &&
            this.isTemplate == newWorkout.isTemplate &&
            this.exercises.any { currExercises ->
                newWorkout.exercises.any { newExercises ->
                    currExercises.equalsTo(newExercises)
                }
            }
}

fun Exercise.equalsTo(exercise: Exercise): Boolean {
    this.isTemplate?.let {
        return this.bodyPart == exercise.bodyPart &&
                this.category == exercise.category &&
                this.exerciseName == exercise.exerciseName
    }
    return this.bodyPart == exercise.bodyPart &&
            this.category == exercise.category &&
            this.exerciseName == exercise.exerciseName &&
            this.sets.all { currSets ->
                exercise.sets.any { newSets ->
                    currSets.equalsTo(newSets)
                }
            }
}

fun Set<Exercise?>.getExerciseDifference(other: Set<Exercise?>): Set<Exercise?> {
    val idToExerciseMap = this.associateBy { it?._id?.toHexString() }
    return other.filter { it?._id?.toHexString() !in idToExerciseMap }.toSet()
}

fun Set<Workout?>.getWorkoutDifference(other: Set<Workout?>): Set<Workout?> {
    val idToWorkoutMap = this.associateBy { it?._id?.toHexString() }
    return other.filter { it?._id?.toHexString() !in idToWorkoutMap }.toSet()
}

fun Sets.equalsTo(newSets: Sets): Boolean {
    return this.firstMetric == newSets.firstMetric &&
            this.secondMetric == newSets.secondMetric
}

inline fun <reified T> RealmList<T>.toFirestoreMap(): List<Map<String, Any?>> {
    return map { item ->
        when (item) {
            is Exercise -> (item as Exercise).toFirestoreMap()
            is Sets -> (item as Sets).toFirestoreMap()
            else -> throw IllegalArgumentException("Unsupported type in RealmList")
        }
    }
}

fun Exercise.toSelectable() = SelectableExercise(this)
fun List<Exercise>.toSelectableList() = this.map { it.toSelectable() }

fun SelectableExercise.toExercise(): Exercise {
    return Exercise().apply {
        _id = exercise._id
        bodyPart = exercise.bodyPart
        category = exercise.category
        exerciseName = exercise.exerciseName
        isTemplate = exercise.isTemplate
        sets = exercise.sets
    }
}

fun List<SelectableExercise>.toExerciseList(): List<Exercise> = this.map { it.toExercise() }

fun Workout.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "_id" to _id.toHexString(),
        "duration" to duration,
        "totalVolume" to totalVolume,
        "numberOfPrs" to numberOfPrs,
        "workoutName" to workoutName,
        "date" to date,
        "exerciseIds" to exerciseIds.map { it },
        "exercises" to exercises.toFirestoreMap(),
        "count" to count,
        "isTemplate" to isTemplate
    )
}

fun Exercise.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "_id" to _id.toHexString(),
        "bodyPart" to bodyPart,
        "category" to category,
        "exerciseName" to exerciseName,
        "isTemplate" to isTemplate,
        "sets" to sets.toFirestoreMap()
    )
}

fun Sets.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "firstMetric" to firstMetric,
        "secondMetric" to secondMetric
    )
}

fun Settings.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "language" to this.language,
        "units" to this.units,
        "soundEffects" to this.soundEffects,
        "theme" to this.theme,
        "restTimer" to this.restTimer,
        "vibration" to this.vibration,
        "soundSettings" to this.soundSettings,
        "updateTemplate" to this.updateTemplate,
        "automaticSync" to this.automaticSync,
        "fit" to this.fit
    )
}

fun View.applyScaleAnimation() {
    val scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 1.4f),
        PropertyValuesHolder.ofFloat("scaleY", 1.4f)
    )
    scaleAnimation.duration = 140
    scaleAnimation.repeatCount = 1
    scaleAnimation.repeatMode = ObjectAnimator.REVERSE

    scaleAnimation.start()
}

fun Filter.equalsTo(filter: Filter): Boolean {
    return this.name == filter.name
}

fun View.applySelectAnimation(
    isSelected: Boolean,
    selectedColorResId: Int,
    unselectedColorResId: Int,
    duration: Long = 300,
) {
    val targetColor = ContextCompat.getColor(
        context,
        if (isSelected) selectedColorResId else unselectedColorResId
    )

    val animator = ObjectAnimator.ofInt(this, "backgroundColor", R.color.background, targetColor)
    animator.setEvaluator(ArgbEvaluator())
    animator.duration = duration
    animator.addUpdateListener { animation -> setBackgroundColor(animation.animatedValue as Int) }
    animator.start()
}

