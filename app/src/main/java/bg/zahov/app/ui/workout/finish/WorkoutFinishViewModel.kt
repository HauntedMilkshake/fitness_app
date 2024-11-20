package bg.zahov.app.ui.workout.finish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutFinishUiState(
    val workout: Workout = Workout(),
    val isLoading: Boolean = true,
    val workoutCount: Int = 0,
)

class WorkoutFinishViewModel(private val workoutProvider: WorkoutProvider = Inject.workoutProvider) :
    ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutFinishUiState())
    val uiState: StateFlow<WorkoutFinishUiState> = _uiState

    init {
        _uiState.update { old ->
            old.copy(isLoading = false, workout = workoutProvider.getLastWorkout() ?: Workout())
        }
        viewModelScope.launch {
            workoutProvider.getPastWorkouts().collect {
                _uiState.update { old ->
                    old.copy(workoutCount = it.size)
                }
            }
        }
    }
}
