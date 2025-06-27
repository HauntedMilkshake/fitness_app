package bg.zahov.app.ui.topbar.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.WorkoutActions
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.data.provider.RestTimerProvider.Rest
import bg.zahov.app.data.provider.WorkoutStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the state of the Workout Top Bar UI.
 *
 * @property elapsedRestTime The elapsed rest time as a formatted string (e.g., "00:01:30").
 * @property elapsedWorkoutTime The elapsed workout time as a formatted string (e.g., "00:25:43").
 * @property progress The progress of the rest timer, represented as a value between 0.0 and 1.0.
 */
data class TopBarWorkoutData(
    val elapsedRestTime: String = "",
    val elapsedWorkoutTime: String = "",
    val progress: Float = 0f,
)

/**
 * ViewModel responsible for managing the state and interactions of the Workout Top Bar UI.
 *
 * @property workoutStateManager Manages workout timer state and provides elapsed workout time.
 * @property restStateManager Manages rest timer state and provides elapsed rest time and total rest duration.
 * @property workoutActionHandler Handles workout-related actions such as finishing the workout.
 *
 * Exposes:
 * - [uiState]: A [StateFlow] containing the UI state ([TopBarWorkoutData]) with elapsed workout time,
 *   elapsed rest time, and rest progress.
 *
 * Automatically collects:
 * - Workout timer updates from [workoutStateManager].
 * - Rest timer updates from [restStateManager], calculating progress for the rest timer.
 */
@HiltViewModel
class TopBarWorkoutViewModel @Inject constructor(
    private val workoutStateManager: WorkoutActions,
    private val restStateManager: RestTimerProvider,
    private val workoutActionHandler: WorkoutProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopBarWorkoutData())
    val workoutTimer: StateFlow<Long> = workoutStateManager.timer
    val restTimer: StateFlow<Rest> = restStateManager.restTimer

    /**
     * The observable state of the Workout Top Bar UI, exposed as a [StateFlow].
     * Contains:
     * - [TopBarWorkoutData.elapsedWorkoutTime]: Elapsed workout time as a formatted string.
     * - [TopBarWorkoutData.elapsedRestTime]: Elapsed rest time as a formatted string.
     * - [TopBarWorkoutData.progress]: Progress of the rest timer as a float between 0 and 1.
     */
    val uiState: StateFlow<TopBarWorkoutData> = _uiState

    /**
     * Calculates the progress of the rest timer as a value between 0 and 1.
     *
     * @param fullRest Total rest time in milliseconds.
     * @param currentRest Current elapsed rest time in milliseconds.
     * @return The calculated progress as a float, constrained between 0 and 1.
     */
    fun calculateProgress(fullRest: Float, currentRest: Float): Float {
        return (currentRest / fullRest).coerceIn(0f, 1f)
    }

    /**
     * Minimizes the workout by invoking the [WorkoutStateManager.minimizeWorkout] function.
     *
     * This is typically triggered by the user interacting with the "Minimize" button
     * in the Workout Top Bar.
     */
    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.minimizeWorkout()
        }
    }

    /**
     * Attempts to finish the workout by invoking the [bg.zahov.app.data.provider.WorkoutProviderImpl.tryToFinish] function.
     *
     * This is typically triggered by the user interacting with the "Finish" button
     * in the Workout Top Bar.
     */
    fun finish() {
        viewModelScope.launch {
            workoutActionHandler.tryToFinish()
        }
    }
}