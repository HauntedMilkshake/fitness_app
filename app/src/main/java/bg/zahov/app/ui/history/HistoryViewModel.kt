package bg.zahov.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.provider.model.HistoryWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @property workouts A list of past workouts to be displayed.
 */
data class HistoryUiState(
    val workouts: List<HistoryWorkout> = listOf(),
)

/**
 * This ViewModel handles the fetching of past workouts and updates the UI state accordingly.
 *
 * @property workoutProvider An instance of [WorkoutProvider] used to retrieve past workouts.
 * @property serviceError An instance of [ServiceErrorHandler] used for handling errors during data retrieval.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutProvider: WorkoutProvider,
    private val serviceError: ServiceErrorHandler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        viewModelScope.launch {
            workoutProvider.getHistoryWorkouts().collect { workouts ->
                try {
                    _uiState.update { old ->
                        old.copy(workouts = workouts.map {
                            it.copy(
                                exercises = it.exercises.take(5),
                                bestSets = it.bestSets.take(5)
                            )
                        })
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.initiateCountdown()
                }
            }
        }
    }

    fun setClickedWorkout(workoutId: String) {
        viewModelScope.launch {
            workoutProvider.getPastWorkouts().collect {
                it.find { workout -> workoutId == workout.id }?.let { workout ->
                    workoutProvider.setClickedHistoryWorkout(workout)
                }
            }
        }
    }
}