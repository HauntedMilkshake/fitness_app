package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutProvider {
    suspend fun getTemplateWorkouts(): Flow<List<Workout>>
    suspend fun getPastWorkouts(): Flow<List<Workout>>
    suspend fun addTemplateWorkout(newWorkout: Workout)
    suspend fun getTemplateExercises(): Flow<List<Exercise>>
    suspend fun addTemplateExercise(newExercise: Exercise)
    suspend fun addWorkoutToHistory(newWorkout:Workout)
    suspend fun deleteTemplateWorkout(workout: Workout)
    suspend fun deleteWorkout(workout: Workout)
    suspend fun getWorkoutById(id: String): Flow<Workout?>
}