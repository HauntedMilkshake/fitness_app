package bg.zahov.app.utils

import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.Sets
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.realm_db.User
import bg.zahov.app.realm_db.Workout
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

fun User.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "username" to username,
        "numberOfWorkouts" to numberOfWorkouts,
        "workouts" to workouts.toMap(),
        "customExercises" to customExercises.toMap(),
        "settings" to settings?.toMap()
    )
}

//fun RealmList<Workout>.toMap(): List<Map<String, Any?>> {
//    return map { workout ->
//        mapOf(
//            "duration" to workout.duration,
//            "totalVolume" to workout.totalVolume,
//            "numberOfPrs" to workout.numberOfPrs,
//            "workoutName" to workout.workoutName,
//            "date" to workout.date,
//            "exercises" to workout.exercises.toMap(),
//            "count" to workout.count
//        )
//    }
//}
//
//private fun RealmList<Exercise>.toMap(): List<Map<String, Any?>> {
//    return map { exercise ->
//        mapOf(
//            "bodyPart" to exercise.bodyPart,
//            "category" to exercise.category,
//            "exerciseName" to exercise.exerciseName,
//            "sets" to exercise.sets.toMap()
//        )
//    }
//}
//
//private fun RealmList<Sets>.toMap(): List<Map<String, Any?>> {
//    return map { set ->
//        mapOf(
//            "firstMetric" to set.firstMetric,
//            "secondMetric" to set.secondMetric
//        )
//    }
//}

fun RealmList<out RealmObject>.toMap(): List<Map<String, Any?>> {
    return map { element ->
        val fieldMap = element.javaClass.declaredFields.associate { field ->
            field.isAccessible = true
            field.name to field.get(element)
        }
        fieldMap.filterValues { it != null }
    }
}

private fun Settings?.toMap(): Map<String, Any?> {
    return mapOf(
        "language" to this?.language,
        "weight" to this?.weight,
        "distance" to this?.distance,
        "soundEffects" to this?.soundEffects,
        "theme" to this?.theme,
        "restTimer" to this?.restTimer,
        "vibration" to this?.vibration,
        "soundSettings" to this?.soundSettings,
        "updateTemplate" to this?.updateTemplate,
        "fit" to this?.fit
    )
}

