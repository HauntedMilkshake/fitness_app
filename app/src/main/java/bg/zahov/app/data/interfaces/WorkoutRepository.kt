package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    suspend fun getTemplateWorkouts(): Flow<List<Workout>>?
    suspend fun getPastWorkouts(): Flow<List<Workout>>?
    suspend fun addWorkout(newWorkout: Workout)
    suspend fun getTemplateExercises(): Flow<List<Exercise>>?
    suspend fun addTemplateExercise(newExercise: Exercise)
}