package bg.zahov.app.data.model

import android.util.Log
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.util.toLocalDateTime
import com.google.firebase.Timestamp
import java.time.LocalDateTime

object FirestoreFields {
    const val USERS = "users"

    // User fields
    const val USER_NAME = "name"
    const val USER_MEASUREMENTS = "measurements"

    const val USER_WORKOUTS = "workouts"
    const val USER_TEMPLATE_WORKOUTS = "templateWorkouts"
    const val USER_TEMPLATE_EXERCISES = "templateExercises"
    const val MEASUREMENTS_COLLECTION = "measurements"
//    const val USER_SETTINGS = "settings"

    // Workout fields
    const val WORKOUT_ID = "id"
    const val WORKOUT_NAME = "name"
    const val WORKOUT_DURATION = "duration"
    const val WORKOUT_VOLUME = "volume"
    const val WORKOUT_DATE = "date"
    const val WORKOUT_IS_TEMPLATE = "isTemplate"
    const val WORKOUT_EXERCISES = "exercises"
    const val WORKOUT_NOTE = "note"
    const val WORKOUT_PERSONAL_RECORD = "personalRecords"

    // Exercise fields
    const val EXERCISE_NAME = "name"
    const val EXERCISE_BODY_PART = "bodyPart"
    const val EXERCISE_CATEGORY = "category"
    const val EXERCISE_IS_TEMPLATE = "template"
    const val EXERCISE_SETS = "sets"
    const val EXERCISE_NOTE = "note"
    const val EXERCISE_BEST_SET = "bestSet"

    // Sets fields
    const val SETS_TYPE = "type"
    const val SETS_FIRST_METRIC = "firstMetric"
    const val SETS_SECOND_METRIC = "secondMetric"

    //Measurements Fields
    const val MEASUREMENT_VALUE = "value"
    const val MEASUREMENT_DATE = "date"

    // Settings fields
//    const val SETTINGS_LANGUAGE = "language"
//    const val SETTINGS_UNITS = "units"
//    const val SETTINGS_SOUND_EFFECTS = "soundEffects"
//    const val SETTINGS_THEME = "theme"
//    const val SETTINGS_REST_TIMER = "restTimer"
//    const val SETTINGS_VIBRATION = "vibration"
//    const val SETTINGS_SOUND_SETTINGS = "soundSettings"
//    const val SETTINGS_UPDATE_TEMPLATE = "updateTemplate"
//    const val SETTINGS_FIT = "fit"
//    const val SETTINGS_AUTOMATIC_SYNC = "automaticSync"
}

data class User(
    var name: String
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?): User = data?.let { firestoreData ->
            User(
                name = (firestoreData[FirestoreFields.USER_NAME] as? String) ?: throw CriticalDataNullException("No user found"))
        } ?: throw CriticalDataNullException("No user found")
    }
}
data class Measurements(
    val measurements: Map<MeasurementType, List<Measurement>> = mapOf()
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?, measurementType: MeasurementType): Measurements {
            val measurementsMap = mutableMapOf<MeasurementType, List<Measurement>>()
            data?.let {
            val measurements = (it[FirestoreFields.MEASUREMENTS_COLLECTION] as? List<Map<String, Any>>)?.mapNotNull { values -> Measurement.fromFirestoreMap(values) }.orEmpty()
            measurementsMap[measurementType] = measurements
            }
            return Measurements(measurementsMap)
        }
    }
}

data class Workout(
    var id: String,
    var name: String,
    var duration: Long?,
    var volume: Double?,
    var date: LocalDateTime,
    var isTemplate: Boolean,
    var exercises: List<Exercise>,
    val note: String? = null,
    var personalRecords: Int = 0,
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?) = data?.let {
            Log.d("personal records", (it[FirestoreFields.WORKOUT_PERSONAL_RECORD] as? Int).toString())
            Workout(
                id = it[FirestoreFields.WORKOUT_ID] as? String
                    ?: throw CriticalDataNullException(""),
                name = it[FirestoreFields.WORKOUT_NAME] as? String
                    ?: throw CriticalDataNullException(""),
                duration = it[FirestoreFields.WORKOUT_DURATION] as? Long,
                volume = it[FirestoreFields.WORKOUT_VOLUME] as? Double,
                date = (it[FirestoreFields.WORKOUT_DATE] as? Timestamp)?.toLocalDateTime()
                    ?: throw CriticalDataNullException(""),
                personalRecords = it[FirestoreFields.WORKOUT_PERSONAL_RECORD] as? Int ?: 0,
                isTemplate = it[FirestoreFields.WORKOUT_IS_TEMPLATE] as? Boolean ?: false,
                exercises = (it[FirestoreFields.WORKOUT_EXERCISES] as List<Map<String, Any>?>)
                    .mapNotNull { map ->
                        Exercise.fromFirestoreMap(map)
                    },
                note = it[FirestoreFields.WORKOUT_NOTE] as? String
            )
        } ?: throw CriticalDataNullException("No firestore data found")
    }
}

data class Exercise(
    var name: String,
    var bodyPart: BodyPart,
    var category: Category,
    var isTemplate: Boolean,
    val sets: MutableList<Sets> = mutableListOf(),
    var bestSet: Sets = Sets(SetType.DEFAULT, null, null),
    var note: String? = null,
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?) = data?.let {
            Exercise(
                name = it[FirestoreFields.EXERCISE_NAME] as? String ?: throw CriticalDataNullException("No name for exercise"),
                bodyPart = it[FirestoreFields.EXERCISE_BODY_PART].toString()
                    .let { string -> BodyPart.valueOf(string) },
                category = it[FirestoreFields.EXERCISE_CATEGORY].toString()
                    .let { string -> Category.valueOf(string) },
                isTemplate = it[FirestoreFields.EXERCISE_IS_TEMPLATE] as Boolean,
                sets = (it[FirestoreFields.EXERCISE_SETS] as List<Map<String, Any>>).mapNotNull { map ->
                    Sets.fromFirestoreMap(
                        map
                    )
                }.toMutableList(),
                note = it[FirestoreFields.EXERCISE_NOTE] as? String,
                bestSet = (it[FirestoreFields.EXERCISE_BEST_SET] as? Map<String, Any>)?.let { map ->
                    Sets.fromFirestoreMap(map)
                } ?: Sets(SetType.DEFAULT, null, null)
            )
        } ?: throw CriticalDataNullException("No exercise found")
    }
}

data class Sets(
    var type: SetType,
    var firstMetric: Double?,
    var secondMetric: Int?,
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?) = data?.let {
            Sets(
                type = (it[FirestoreFields.SETS_TYPE] as? String)?.let { string ->
                    SetType.fromKey(
                        string
                    )
                } ?: SetType.DEFAULT,
                firstMetric = it[FirestoreFields.SETS_FIRST_METRIC] as? Double,
                secondMetric = (it[FirestoreFields.SETS_SECOND_METRIC] as? Long)?.toInt()
            )
        }
    }
}

data class Measurement(
    val date: LocalDateTime,
    val value: Double,
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?): Measurement? {
            return if (data != null && (data[FirestoreFields.WORKOUT_DATE] as? Timestamp) != null && (data[FirestoreFields.MEASUREMENT_VALUE] as? Double) != null) {
                Measurement(
                    date = (data[FirestoreFields.MEASUREMENT_DATE] as Timestamp).toLocalDateTime(),
                    value = data[FirestoreFields.MEASUREMENT_VALUE] as Double
                )
            } else {
                null
            }
        }
    }
}
