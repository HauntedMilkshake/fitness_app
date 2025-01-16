package bg.zahov.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.data.provider.WorkoutStateManager
import bg.zahov.app.util.timeToString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents the UI state for the Workout screen.
 *
 * @property trailingWorkoutVisibility Determines whether the minimized workout UI should be visible.
 * @property isWorkoutActive Indicates whether a workout is currently active.
 * @property workoutName The name of the current workout template.
 * @property timer A string representation of the workout timer.
 */
data class WorkoutUiState(
    val trailingWorkoutVisibility: Boolean = false,
    val isWorkoutActive: Boolean = false,
    val workoutName: String = "",
    val timer: String = "",
)

/**
 * ViewModel responsible for managing the state of the Workout UI.
 *
 * This ViewModel interacts with [WorkoutStateManager] to observe and update the workout state,
 * such as the workout timer, workout activity status, and the visibility of UI components.
 *
 * @property workoutStateManager Provides workout state and actions to modify it.
 */
class WorkoutManagerViewModel(
    private val workoutStateManager: WorkoutStateManager = Inject.workoutState,
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutUiState())

    /**
     * Publicly exposed immutable state for the Workout UI.
     */
    val state: StateFlow<WorkoutUiState>
        get() = _state

    /**
     * Initializes the ViewModel by collecting updates from the [WorkoutStateManager].
     */
    init {
        viewModelScope.launch {
            launch {
                workoutStateManager.template.collect {
                    it?.let {
                        _state.update { old ->
                            old.copy(workoutName = it.name)
                        }
                    }
                }
            }

            launch {
                workoutStateManager.state.collect {
                    _state.update { old ->
                        old.copy(
                            trailingWorkoutVisibility = (it == WorkoutState.MINIMIZED),
                            isWorkoutActive = (it == WorkoutState.ACTIVE)
                        )
                    }
                }
            }

            launch {
                workoutStateManager.timer.collect {
                    _state.update { old ->
                        old.copy(timer = it.timeToString())
                    }
                }
            }
        }
    }

    /**
     * Activates the workout state by starting a new workout.
     */
    fun updateStateToActive() {
        viewModelScope.launch {
            workoutStateManager.startWorkout(null)
        }
    }

    /**
     * Saves the current workout state to persistent storage.
     */
    fun saveWorkoutState() {
        viewModelScope.launch {
            workoutStateManager.saveWorkout()
        }
    }
}