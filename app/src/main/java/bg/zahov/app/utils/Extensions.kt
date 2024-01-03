package bg.zahov.app.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.Sets
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.realm_db.User
import bg.zahov.app.realm_db.Workout
import io.realm.kotlin.types.RealmList

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
            this.units == newSettings.units
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
