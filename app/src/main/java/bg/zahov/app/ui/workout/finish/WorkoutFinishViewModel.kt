package bg.zahov.app.ui.workout.finish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.provider.model.HistoryWorkout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents the UI state for the workout finish screen.
 *
 * @property workout The most recently completed workout, represented as a [HistoryWorkout].
 * @property workoutCount A string representation of the total number of past workouts.
 */
data class WorkoutFinishUiState(
    val workout: HistoryWorkout = HistoryWorkout(),
    val workoutCount: String = "",
)

/**
 * @property workoutProvider A provider that supplies workout data, including the last workout
 * and the history of past workouts.
 */
class WorkoutFinishViewModel(
    private val workoutProvider: WorkoutProvider = Inject.workoutProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutFinishUiState())

    /**
     * Publicly exposed [StateFlow] representing the current state of the UI.
     * Observers can collect this flow to react to changes in the UI state.
     */
    val uiState: StateFlow<WorkoutFinishUiState> = _uiState

    /**
     * Initializes the ViewModel by fetching the last workout and observing the list of past workouts.
     */
    init {
        _uiState.update { old ->
            old.copy(workout = workoutProvider.getLastWorkout() ?: HistoryWorkout())
        }

        viewModelScope.launch {
            workoutProvider.getPastWorkouts().collect { pastWorkouts ->
                _uiState.update { old ->
                    old.copy(workoutCount = pastWorkouts.size.toString())
                }
            }
        }
    }
}