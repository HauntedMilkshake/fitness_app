package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.WorkoutRepository
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.remote.FirestoreManager
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class WorkoutRepositoryImpl : WorkoutRepository {
    companion object {
        @Volatile
        private var instance: WorkoutRepositoryImpl? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: WorkoutRepositoryImpl().also { instance = it }
            }
    }

    private val firestore = FirestoreManager.getInstance()

    override suspend fun getTemplateWorkouts(): Flow<List<Workout>> =
        firestore.getTemplateWorkouts()

    override suspend fun getPastWorkouts(): Flow<List<Workout>> = firestore.getWorkouts()

    override suspend fun addTemplateWorkout(newWorkout: Workout) {
        firestore.addTemplateWorkout(newWorkout)
    }

    override suspend fun getTemplateExercises(): Flow<List<Exercise>> =
        firestore.getTemplateExercises()

    override suspend fun addTemplateExercise(newExercise: Exercise) {
        firestore.addTemplateExercise(newExercise)
    }

    override suspend fun addWorkoutToHistory(newWorkout: Workout) {
        firestore.addWorkoutToHistory(newWorkout)
    }

    override suspend fun deleteTemplateWorkout(workout: Workout) {
        firestore.deleteTemplateWorkouts(workout)
    }

    override suspend fun deleteWorkout(workout: Workout) {
        firestore.deleteWorkout(workout)
    }

    override suspend fun getWorkoutById(id: String) = firestore.getWorkoutById(id)
    override suspend fun getWorkoutByName(name: String): Flow<Workout> =
        firestore.getTemplateWorkoutByName(name)

    override suspend fun updateExercises(exercises: List<Exercise>) {
        firestore.updateExerciseInBatch(exercises)
    }

    override suspend fun getPastWorkoutById(id: String): Workout = firestore.getPastWorkoutById(id)

    override suspend fun updateTemplateWorkout(
        workoutId: String,
        date: LocalDateTime,
        newExercise: List<Exercise>,
    ) {
        firestore.updateTemplateWorkout(workoutId, date, newExercise)
    }

    override suspend fun clearWorkoutState() {
        //
    }
}