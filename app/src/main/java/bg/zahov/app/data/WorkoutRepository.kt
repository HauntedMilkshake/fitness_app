package bg.zahov.app.data

import bg.zahov.app.data.local.Exercise
import bg.zahov.app.data.local.RealmManager
import bg.zahov.app.data.local.Workout

class WorkoutRepository {
    companion object {
        @Volatile
        private var instance: WorkoutRepository? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: WorkoutRepository().also { instance = it }
            }
    }

    private val realm = RealmManager.getInstance()

    suspend fun getTemplateWorkouts() = realm.getWorkouts(true)
    suspend fun getPastWorkouts() = realm.getWorkouts(false)
    suspend fun addWorkout(newWorkout: Workout) = realm.addWorkout(newWorkout)

    suspend fun addTemplateExercise(newExercise: Exercise) = realm.addExercise(newExercise)
    suspend fun getTemplateExercises() = realm.getExercises(true)

}