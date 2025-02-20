package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WorkoutActions {
    val shouldSave: Flow<Boolean>
    val state: StateFlow<WorkoutState>
    val template: StateFlow<Workout?>
    val timer: StateFlow<Long>
    suspend fun <T> startWorkout(workout: T? = null, lastTime: Long? = null, isResuming: Boolean = false)
    suspend fun finishWorkout()
    suspend fun minimizeWorkout()
    suspend fun cancel()
    suspend fun clear()
    suspend fun saveWorkout()
}