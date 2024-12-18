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

data class WorkoutUiState(
    val trailingWorkoutVisibility: Boolean = false,
    val workoutName: String = "",
    val timer: String = ""
)

class WorkoutManagerViewModel(
    private val workoutStateManager: WorkoutStateManager = Inject.workoutState,
) : ViewModel() {

    private val _state = MutableStateFlow<WorkoutUiState>(WorkoutUiState())
    val state: StateFlow<WorkoutUiState>
        get() = _state

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
                        old.copy(trailingWorkoutVisibility = (it == WorkoutState.MINIMIZED))
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

    fun updateStateToActive() {
        viewModelScope.launch {
            workoutStateManager.startWorkout(null)
        }
    }

    fun saveWorkoutState() {
        viewModelScope.launch {
            workoutStateManager.saveWorkout()
        }
    }
}
