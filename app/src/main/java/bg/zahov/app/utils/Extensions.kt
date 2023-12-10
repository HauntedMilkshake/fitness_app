package bg.zahov.app.utils

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
        "weight" to this.weight,
        "distance" to this.distance,
        "soundEffects" to this.soundEffects,
        "theme" to this.theme,
        "restTimer" to this.restTimer,
        "vibration" to this.vibration,
        "soundSettings" to this.soundSettings,
        "updateTemplate" to this.updateTemplate,
        "fit" to this.fit
    )
}
