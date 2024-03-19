package bg.zahov.app.data.model

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
    const val WORKOUT_PERSONAL_RECORD = "personal_record"

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
    var name: String,
    val measurements: Map<MeasurementType, List<Measurement>> = mapOf(),
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?): User = data?.let { firestoreData ->
            User(
                name = (firestoreData[FirestoreFields.USER_NAME] as? String)
                    ?: throw CriticalDataNullException("No user found"),
                measurements = (firestoreData
                    [FirestoreFields.USER_MEASUREMENTS] as? Map<String, List<Map<String, Any>>>)
                    ?.mapKeys { (key, _) ->
                        MeasurementType.valueOf(key)
                    }
                    ?.mapValues { (_, value) ->
                        value.map { measurement ->  Measurement.fromFirestoreMap(measurement) }
                    } ?: emptyMap()
            )
        } ?: throw CriticalDataNullException("No user found")
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
    val personalRecords: Int = 0,
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?) = data?.let {
            Workout(
                id = it[FirestoreFields.WORKOUT_ID] as? String
                    ?: throw CriticalDataNullException(""),
                name = it[FirestoreFields.WORKOUT_NAME] as? String
                    ?: throw CriticalDataNullException(""),
                duration = it[FirestoreFields.WORKOUT_DURATION] as? Long,
                date = (it[FirestoreFields.WORKOUT_DATE] as? Timestamp)?.toLocalDateTime()
                    ?: throw CriticalDataNullException(""),
                isTemplate = it[FirestoreFields.WORKOUT_IS_TEMPLATE] as? Boolean ?: false,
                exercises = (it[FirestoreFields.WORKOUT_EXERCISES] as List<Map<String, Any>?>)
                    .mapNotNull { map ->
                        Exercise.fromFirestoreMap(map)
                    },
                note = it[FirestoreFields.WORKOUT_NOTE] as? String,
                volume = it[FirestoreFields.WORKOUT_VOLUME] as? Double,
                personalRecords = it[FirestoreFields.WORKOUT_PERSONAL_RECORD] as? Int ?: 0
            )
        } ?: throw CriticalDataNullException("No firestore data found")
        //TODO(RETURN THIS INSTEAD OF THROWING)
        //?: Workout("", null, "", false, emptyList(), emptyList())
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
                name = it[FirestoreFields.EXERCISE_NAME] as String,
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
        }
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
        fun fromFirestoreMap(data: Map<String, Any>?) = data?.let {
            Measurement(
                date = (it[FirestoreFields.WORKOUT_DATE] as? Timestamp
                    ?: Timestamp.now()).toLocalDateTime(),
                value = (it[FirestoreFields.MEASUREMENT_VALUE] as? Double ?: 0.0)
            )
        } ?: throw CriticalDataNullException("No data received")
    }
}

//data class Settings(
//    var language: String = Language.fromKey(LanguageKeys.ENGLISH),
//    var units: String = Units.fromKey(UnitsKeys.METRIC),
//    var soundEffects: Boolean = true,
//    var theme: String = Theme.fromKey(ThemeKeys.DARK),
//    var restTimer: Int = 30,
//    var vibration: Boolean = true,
//    var soundSettings: String = Sound.fromKey(SoundKeys.SOUND_1),
//    var updateTemplate: Boolean = true,
//    var fit: Boolean = false,
//    var automaticSync: Boolean = true
//) {
//    companion object {
//        fun fromFirestoreMap(data: Map<String, Any>): Settings {
//            return Settings(
//                language = data[FirestoreFields.SETTINGS_LANGUAGE] as String,
//                units = data[FirestoreFields.SETTINGS_UNITS] as String,
//                soundEffects = data[FirestoreFields.SETTINGS_SOUND_EFFECTS] as Boolean,
//                theme = data[FirestoreFields.SETTINGS_THEME] as String,
//                restTimer = (data[FirestoreFields.SETTINGS_REST_TIMER] as Long).toInt(),
//                vibration = data[FirestoreFields.SETTINGS_VIBRATION] as Boolean,
//                soundSettings = data[FirestoreFields.SETTINGS_SOUND_SETTINGS] as String,
//                updateTemplate = data[FirestoreFields.SETTINGS_UPDATE_TEMPLATE] as Boolean,
//                fit = data[FirestoreFields.SETTINGS_FIT] as Boolean,
//                automaticSync = data[FirestoreFields.SETTINGS_AUTOMATIC_SYNC] as Boolean
//            )
//        }
//    }
//}