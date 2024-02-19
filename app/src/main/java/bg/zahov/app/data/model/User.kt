package bg.zahov.app.data.model

object FirestoreFields {
    const val USERS = "users"

    // User fields
    const val USER_NAME = "name"
    const val USER_WORKOUTS = "workouts"
    const val USER_TEMPLATE_WORKOUTS = "templateWorkouts"
    const val USER_TEMPLATE_EXERCISES = "templateExercises"
//    const val USER_SETTINGS = "settings"

    // Workout fields
    const val WORKOUT_ID = "id"
    const val WORKOUT_NAME = "name"
    const val WORKOUT_DURATION = "duration"
    const val WORKOUT_DATE = "date"
    const val WORKOUT_IS_TEMPLATE = "isTemplate"
    const val WORKOUT_EXERCISES = "exercises"
    const val WORKOUT_NOTE = "note"

    // Exercise fields
    const val EXERCISE_NAME = "name"
    const val EXERCISE_BODY_PART = "bodyPart"
    const val EXERCISE_CATEGORY = "category"
    const val EXERCISE_IS_TEMPLATE = "template"
    const val EXERCISE_SETS = "sets"
    const val EXERCISE_NOTE = "note"

    // Sets fields
    const val SETS_TYPE = "type"
    const val SETS_FIRST_METRIC = "firstMetric"
    const val SETS_SECOND_METRIC = "secondMetric"

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
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?) =
            data?.let { User(name = it[FirestoreFields.USER_NAME] as String) }
    }
}

data class Workout(
    var id: String,
    var name: String,
    var duration: Double?,
    var date: String,
    var isTemplate: Boolean,
    var exercises: List<Exercise>,
    val note: String? = null
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?) = data?.let {
            Workout(
                id = it[FirestoreFields.WORKOUT_ID] as String,
                name = it[FirestoreFields.WORKOUT_NAME] as String,
                duration = it[FirestoreFields.WORKOUT_DURATION] as? Double,
                date = it[FirestoreFields.WORKOUT_DATE] as String,
                isTemplate = it[FirestoreFields.WORKOUT_IS_TEMPLATE] as Boolean,
                exercises = (it[FirestoreFields.WORKOUT_EXERCISES] as List<Map<String, Any>>)
                    .mapNotNull { map ->
                        Exercise.fromFirestoreMap(map)
                    },
                note = (it[FirestoreFields.WORKOUT_NOTE]) as? String
            )
        }
        //TODO(RETURN THIS INSTEAD OF THROWING)
        //?: Workout("", null, "", false, emptyList(), emptyList())
    }
}


data class Exercise(
    var name: String,
    var bodyPart: BodyPart,
    var category: Category,
    var isTemplate: Boolean,
    var sets: List<Sets>,
    var note: String?,
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
                },
                note = it[FirestoreFields.EXERCISE_NOTE] as? String
            )
        }
    }
}

data class Sets(
    var type: String,
    var firstMetric: Double?,
    var secondMetric: Int?,
) {
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>?) = data?.let {
            Sets(
                type = it[FirestoreFields.SETS_TYPE] as String,
                firstMetric = it[FirestoreFields.SETS_FIRST_METRIC] as? Double,
                secondMetric = (it[FirestoreFields.SETS_SECOND_METRIC] as? Long)?.toInt()
            )
        }
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