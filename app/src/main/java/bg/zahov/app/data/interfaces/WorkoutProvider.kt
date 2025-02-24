package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.data.model.state.ExerciseHistoryInfo
import bg.zahov.app.data.provider.model.HistoryInfoWorkout
import bg.zahov.app.data.provider.model.HistoryWorkout
import bg.zahov.app.ui.workout.start.StartWorkout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

interface WorkoutProvider {

    val shouldAddTemplate: Flow<Boolean>

    /**
     * observable exercise that was clicked in history screen
     */
    val clickedPastWorkout: StateFlow<HistoryInfoWorkout>

    /**
     * A [StateFlow] indicating whether a finish attempt has been signaled.
     * The value is `true` when a finish attempt is in progress, and `false` otherwise.
     */
    val shouldFinish: StateFlow<Boolean>

    /**
     * Tracks whether the "Save as Template" action should be triggered.
     */
    val shouldSaveAsTemplate: StateFlow<Boolean>

    /**
     * Tracks whether the "Delete History Workout" action should be triggered.
     */
    val shouldDeleteHistoryWorkout: StateFlow<Boolean>

    suspend fun getTemplateWorkouts(): Flow<List<Workout>>
    suspend fun getPastWorkouts(): Flow<List<Workout>>
    suspend fun addTemplateWorkout(newWorkout: Workout)
    suspend fun getTemplateExercises(): Flow<List<Exercise>>
    suspend fun getExerciseByName(name: String): Flow<Exercise?>
    suspend fun getExercisesByNames(names: List<String>): Flow<List<Exercise>>
    suspend fun getWrappedExercises(): Flow<List<ExerciseData>>
    suspend fun addTemplateExercise(newExercise: Exercise)
    suspend fun addWorkoutToHistory(newWorkout: Workout)
    suspend fun deleteTemplateWorkout(workout: Workout)
    suspend fun deleteWorkout(workout: Workout)
    suspend fun getWorkoutById(id: String): Flow<Workout>
    suspend fun updateExercises(exercises: List<Exercise>)
    suspend fun getTemplateWorkoutByName(name: String): Flow<Workout>
    suspend fun getPastWorkoutById(id: String): Workout
    suspend fun setClickedTemplateExercise(item: ExerciseData)
    suspend fun getClickedTemplateExercise(): Flow<Exercise>
    suspend fun getExerciseHistory(): Flow<List<ExerciseHistoryInfo>>
    suspend fun <T> getPreviousWorkoutState(): T?
    suspend fun <T> addWorkoutState(realmWorkoutState: T)
    suspend fun updateTemplateWorkout(
        workoutId: String,
        date: LocalDateTime,
        newExercises: List<Exercise>,
    )

    /**
     * Triggers the "Save as Template" action.
     */
    fun triggerSaveAsTemplate()

    /**
     * Triggers the "Delete History Workout" action.
     */
    fun triggerDeleteHistoryWorkout()

    /**
     * converts the workout to [HistoryInfoWorkout] and workout
     *
     * @param workout the workout we want to change
     */
    suspend fun setClickedHistoryWorkout(workout: Workout)

    /**
     * Retrieves the list of template workouts mapped for the ui of the appropriate screen from the repository.
     */
    suspend fun getStartWorkouts(): Flow<List<StartWorkout>>

    /**
     * Filters from all workouts only those who were performed in the last month
     */
    suspend fun getCurrentMonthWorkouts(): Flow<List<Workout>>

    suspend fun getHistoryWorkouts(): Flow<List<HistoryWorkout>>

    suspend fun clearWorkoutState()

    /**
     * Returns the last performed workout if any
     */
    fun getLastWorkout(): HistoryWorkout?

    /**
     * Attempts a finish attempt for the workout
     */
    fun tryToFinish()

    fun triggerAddTemplate()
}