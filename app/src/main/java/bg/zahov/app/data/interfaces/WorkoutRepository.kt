package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

//TODO(would be good for addWorkout and addTemplateExercise to return a task or something)
interface WorkoutRepository {
    suspend fun getTemplateWorkouts(): Flow<List<Workout>>
    suspend fun getPastWorkouts(): Flow<List<Workout>>
    suspend fun addTemplateWorkout(newWorkout: Workout)
    suspend fun getTemplateExercises(): Flow<List<Exercise>>
    suspend fun addTemplateExercise(newExercise: Exercise)
    suspend fun addWorkoutToHistory(newWorkout: Workout)
    suspend fun deleteTemplateWorkout(workout: Workout)
    suspend fun deleteWorkout(workout: Workout)
    suspend fun getWorkoutById(id: String): Flow<Workout>
    suspend fun getWorkoutByName(name: String): Flow<Workout>
    suspend fun updateExercises(exercises: List<Exercise>)
    suspend fun getPastWorkoutById(id: String): Workout
    suspend fun updateTemplateWorkout(workoutId: String, date: LocalDateTime, newExercise: List<Exercise>)
    suspend fun clearWorkoutState()
}