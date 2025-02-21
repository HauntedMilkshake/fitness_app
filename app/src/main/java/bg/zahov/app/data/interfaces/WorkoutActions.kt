package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the state and lifecycle of an active workout session.
 */
interface WorkoutActions {

    /**
     * Emits `true` when the workout should be saved, otherwise `false`.
     */
    val shouldSave: Flow<Boolean>

    /**
     * Represents the current state of the workout session.
     */
    val state: Flow<WorkoutState>

    /**
     * Holds the active workout template, if available.
     */
    val template: StateFlow<Workout?>

    /**
     * Tracks the workout duration in milliseconds.
     */
    val timer: StateFlow<Long>

    /**
     * Starts a workout session.
     *
     * @param T The type of workout.
     * @param workout The workout data to start with, or `null` for a new session.
     * @param lastTime The timestamp of the last session, if resuming.
     * @param isResuming `true` if resuming a previous workout, `false` otherwise.
     */
    suspend fun <T> startWorkout(
        workout: T? = null,
        lastTime: Long? = null,
        isResuming: Boolean = false,
    )

    /**
     * Marks the workout as finished and processes the results.
     */
    suspend fun finishWorkout()

    /**
     * Minimizes the workout session without ending it.
     */
    suspend fun minimizeWorkout()

    /**
     * Cancels the current workout session.
     */
    suspend fun cancel()

    /**
     * Clears the workout session state.
     */
    suspend fun clear()

    /**
     * Saves the current workout progress.
     */
    suspend fun saveWorkout()
}
