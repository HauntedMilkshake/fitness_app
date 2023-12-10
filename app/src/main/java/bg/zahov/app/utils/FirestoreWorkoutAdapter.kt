package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.realm_db.Workout
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList

class FirestoreWorkoutAdapter : Adapter<Map<String, Any>, Workout> {
    override fun adapt(t: Map<String, Any>): Workout? {
        return Workout().apply {
            t?.let {
                duration = it["duration"] as? Double
                totalVolume = it["totalVolume"] as? Double
                numberOfPrs = it["numberOfPrs"] as? Int
                workoutName = it["workoutName"] as? String
                date = it["date"] as? String
                count = it["count"] as? Int
                isTemplate = it["isTemplate"] as? Boolean

                val exerciseIdsList = it["exerciseIds"] as? List<String> ?: emptyList()
                exerciseIds = realmListOf(*exerciseIdsList.toTypedArray())

                val exercisesList = it["exercises"] as? List<Map<String, Any>> ?: emptyList()
                exercises = exercisesList.mapNotNull { exerciseMap -> FirestoreExerciseAdapter().adapt(exerciseMap) }.toRealmList()
            }
        }
    }
}
