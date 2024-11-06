package bg.zahov.app.data.interfaces

import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.provider.model.HistoryWorkout
import bg.zahov.app.ui.exercise.ExercisesWrapper
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface WorkoutProvider {
    suspend fun getTemplateWorkouts(): Flow<List<Workout>>
    suspend fun getPastWorkouts(): Flow<List<Workout>>
    suspend fun addTemplateWorkout(newWorkout: Workout)
    suspend fun getTemplateExercises(): Flow<List<Exercise>>
    suspend fun getExerciseByName(name: String): Exercise?
    suspend fun getExercisesByWrapper(exercises:List<ExercisesWrapper>): List<Exercise>
    suspend fun getWrappedExercises(): Flow<List<ExercisesWrapper>>
    suspend fun addTemplateExercise(newExercise: Exercise)
    suspend fun addWorkoutToHistory(newWorkout: Workout)
    suspend fun deleteTemplateWorkout(workout: Workout)
    suspend fun getCurrentMonthWorkouts(): Flow<List<Workout>>
    suspend fun deleteWorkout(workout: Workout)
    suspend fun getWorkoutById(id: String): Flow<Workout>
    suspend fun updateExercises(exercises: List<Exercise>)
    suspend fun getTemplateWorkoutByName(name: String): Flow<Workout>
    suspend fun getPastWorkoutById(id: String): Workout
    suspend fun setClickedTemplateExercise(item: ExercisesWrapper)
    suspend fun getClickedTemplateExercise(): Flow<Exercise>
    suspend fun getExerciseHistory(): Flow<List<ExerciseHistoryInfo>>
    suspend fun getPreviousWorkoutState(): RealmWorkoutState?
    suspend fun addWorkoutState(realmWorkoutState: RealmWorkoutState)
    suspend fun updateTemplateWorkout(
        workoutId: String,
        date: LocalDateTime,
        newExercises: List<Exercise>,
    )

    suspend fun getHistoryWorkouts(): Flow<List<HistoryWorkout>>

    suspend fun clearWorkoutState()
}