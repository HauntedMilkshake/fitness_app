package bg.zahov.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutTopBarHandler
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.data.provider.WorkoutStateManager
import bg.zahov.app.util.parseTimeStringToLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class WorkoutTopBarData(
    val elapsedRestTime: String = "",
    val elapsedWorkoutTime: String = "",
    val progress: Float = 0f,
)

class WorkoutTopBarViewModel(
    private val workoutStateManager: WorkoutStateManager = Inject.workoutState,
    private val restStateManager: RestTimerProvider = Inject.restTimerProvider,
    private val workoutTopBarHandler: WorkoutTopBarHandler = Inject.workoutTopAppHandler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutTopBarData())
    val uiState: StateFlow<WorkoutTopBarData> = _uiState

    init {
        viewModelScope.launch {
            workoutStateManager.timer.collect {
                _uiState.update { old ->
                    old.copy(
                        elapsedWorkoutTime = it.toRestTime()
                    )
                }
            }

            restStateManager.restTimer.collect {
                if (it.elapsedTime != null && it.fullRest != null) {
                    _uiState.update { old ->
                        old.copy(
                            elapsedRestTime = it.elapsedTime ?: "",
                            progress = calculateProgress(
                                fullRest = (it.fullRest?.parseTimeStringToLong()?.toFloat() ?: 0f),
                                currentRest = (it.elapsedTime?.parseTimeStringToLong()?.toFloat()
                                    ?: 0f)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun calculateProgress(fullRest: Float, currentRest: Float): Float {
        return (fullRest / currentRest).coerceIn(0f, 1f)
    }

    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.minimizeWorkout()
        }
    }

    fun finish() {
        viewModelScope.launch {
            workoutTopBarHandler.tryToFinish()
        }
    }
}