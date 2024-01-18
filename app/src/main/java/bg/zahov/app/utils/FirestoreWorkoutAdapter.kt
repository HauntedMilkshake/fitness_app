package bg.zahov.app.utils

import bg.zahov.app.util.Adapter
import bg.zahov.app.data.local.Workout
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.ObjectId

//FIXME replace these adapters with factory method in entity, also - use constants instead of hardcoded strings
class FirestoreWorkoutAdapter : Adapter<Map<String, Any>?, Workout?> {
    override fun adapt(t: Map<String, Any>?): Workout {
        return Workout().apply {
            t?.let {
                _id = it["_id"] as? ObjectId ?: ObjectId()
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
                exercises =
                    exercisesList.map { exerciseMap -> FirestoreExerciseAdapter().adapt(exerciseMap) }
                        .toRealmList()
            }
        }
    }
}
