package bg.zahov.app.data.interfaces

import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryInfo
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.flow.Flow

interface WorkoutProvider {
    suspend fun getTemplateWorkouts(): Flow<List<Workout>>
    suspend fun getPastWorkouts(): Flow<List<Workout>>
    suspend fun addTemplateWorkout(newWorkout: Workout)
    suspend fun getTemplateExercises(): Flow<List<Exercise>>
    suspend fun addTemplateExercise(newExercise: Exercise)
    suspend fun addWorkoutToHistory(newWorkout: Workout)
    suspend fun deleteTemplateWorkout(workout: Workout)
    suspend fun deleteWorkout(workout: Workout)
    suspend fun getWorkoutById(id: String): Flow<Workout>
    suspend fun updateExercises(exercises: List<Exercise>)
    suspend fun getTemplateWorkoutByName(name: String): Flow<Workout>
    suspend fun getPastWorkoutById(id: String): Workout
    fun setClickedTemplateExercise(item: Exercise)
    fun getClickedTemplateExercise(): Exercise
    suspend fun getExerciseHistory(): Flow<List<ExerciseHistoryInfo>>
    suspend fun getPreviousWorkoutState(): RealmWorkoutState?
    suspend fun addWorkoutState(realmWorkoutState: RealmWorkoutState)
    suspend fun clearWorkoutState()
}