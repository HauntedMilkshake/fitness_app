package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.Sets
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.realm_db.User
import bg.zahov.app.realm_db.Workout
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList

class FireStoreAdapter : Adapter<Map<String, Any>, User> {
    override fun adapt(t: Map<String, Any>): User {
        return User().apply {
            username = t["username"] as? String
            numberOfWorkouts = (t["numberOfWorkouts"] as? Long)?.toInt()

            workouts = (t["workouts"] as? List<*>)?.mapNotNull { workoutData ->
                (workoutData as? Map<*, *>)?.let {
                    Workout().apply {
                        duration = it["duration"] as? Double
                        totalVolume = it["totalVolume"] as? Double
                        numberOfPrs = (it["numberOfPrs"] as? Long)?.toInt()
                        workoutName = it["workoutName"] as? String
                        date = it["date"] as? String
                        count = it["count"] as? Int

                        exercises = (it["exercises"] as? List<*>)?.mapNotNull { exerciseData ->
                            (exerciseData as? Map<*, *>)?.let {
                                Exercise().apply {
                                    bodyPart = it["bodyPart"] as? String
                                    category = it["category"] as? String
                                    exerciseName = it["exerciseName"] as? String

                                    // Extracting data for Sets
                                    sets = (it["sets"] as? List<*>)?.mapNotNull { setData ->
                                        (setData as? Map<*, *>)?.let {
                                            Sets().apply {
                                                firstMetric = (it["firstMetric"] as? Long)?.toInt()
                                                secondMetric =
                                                    (it["secondMetric"] as? Long)?.toInt()
                                            }
                                        }
                                    }?.toRealmList() ?: realmListOf()
                                }
                            }
                        }?.toRealmList() ?: realmListOf()
                    }
                }
            }?.toRealmList() ?: realmListOf()

            customExercises = (t["customExercises"] as? List<*>)?.mapNotNull { exerciseData ->
                (exerciseData as? Map<*, *>)?.let {
                    Exercise().apply {
                        bodyPart = it["bodyPart"] as? String
                        category = it["category"] as? String
                        exerciseName = it["exerciseName"] as? String

                        // Extracting data for Sets
                        sets = (it["sets"] as? List<*>)?.mapNotNull { setData ->
                            (setData as? Map<*, *>)?.let {
                                Sets().apply {
                                    firstMetric = (it["firstMetric"] as? Long)?.toInt()
                                    secondMetric = (it["secondMetric"] as? Long)?.toInt()
                                }
                            }
                        }?.toRealmList() ?: realmListOf()
                    }
                }
            }?.toRealmList() ?: realmListOf()

            val settingsData = t["settings"] as? Map<*, *>
            settings = Settings().apply {
                language = settingsData?.get("language") as? String ?: Language.English.name
                weight = settingsData?.get("weight") as? String ?: Units.Metric.name
                distance = settingsData?.get("distance") as? String ?: Units.Metric.name
                soundEffects = settingsData?.get("soundEffects") as? Boolean ?: true
                theme = settingsData?.get("theme") as? String ?: Theme.Dark.name
                restTimer = (settingsData?.get("restTimer") as? Long)?.toInt() ?: 30
                vibration = settingsData?.get("vibration") as? Boolean ?: true
                soundSettings = settingsData?.get("soundSettings") as? String ?: Sound.SOUND_1.name
                updateTemplate = settingsData?.get("updateTemplate") as? Boolean ?: true
                fit = settingsData?.get("fit") as? Boolean ?: false
            }
        }
    }

}