package bg.zahov.app.ui.exercise.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.state.ExerciseHistoryData
import bg.zahov.app.data.model.state.ExerciseHistoryInfo
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class for managing the UI state and business logic of the Exercise Info screen.
 *
 * @property workoutProvider The provider that fetches exercise history data. By default, it uses the injected instance from [Inject.workoutProvider].
 */
@HiltViewModel
class ExerciseInfoViewModel @Inject constructor(private val workoutProvider: WorkoutProvider) :
    ViewModel() {
    //Internal state flow for managing UI data updates.
    private val _uiState = MutableStateFlow(ExerciseHistoryData())

    /**
     * Public state flow that exposes the UI state to observers.
     */
    val uiState: StateFlow<ExerciseHistoryData> = _uiState

    init {
        viewModelScope.launch {
            workoutProvider.getExerciseHistory().collect { data ->
                _uiState.update { old ->
                    old.copy(
                        exerciseHistory = data,
                        maxVolume = processMaxVolume(old.maxVolume, data),
                        oneRepMaxEst = processOneRepMax(old.oneRepMaxEst, data),
                        maxRep = processMaxRep(old.maxRep, data)
                    )
                }
            }
        }
    }

    /**
     * Processes the data to compute max volume chart entries.
     *
     * @param currentState The current state of max volume.
     * @param data The exercise history data.
     * @return An updated state for max volume.
     */
    private fun processMaxVolume(
        currentState: LineChartData,
        data: List<ExerciseHistoryInfo>,
    ): LineChartData {
        val maxVolume = data.flatMap { sets ->
            sets.sets.map {
                Entry(
                    sets.date.dayOfMonth.toFloat(),
                    (it.secondMetric ?: 0).toFloat()
                )
            }
        }.sortedBy { it.x }
        return currentState.copy(
            maxValue = maxVolume.maxOfOrNull { it.y } ?: 0f,
            minValue = maxVolume.minOfOrNull { it.y } ?: 0f,
            list = maxVolume
        )
    }

    /**
     * Processes the data to compute one-rep max chart entries.
     *
     * @param currentState The current state of one-rep max.
     * @param data The exercise history data.
     * @return An updated state for one-rep max.
     */
    private fun processOneRepMax(
        currentState: LineChartData,
        data: List<ExerciseHistoryInfo>,
    ): LineChartData {
        val oneRepMax = data.flatMap { sets ->
            sets.oneRepMaxes.map {
                Entry(
                    sets.date.dayOfMonth.toFloat(),
                    it.toFloat()
                )
            }
        }.sortedBy { it.x }
        return currentState.copy(
            maxValue = oneRepMax.maxOfOrNull { it.y } ?: 0f,
            minValue = oneRepMax.minOfOrNull { it.y } ?: 0f,
            list = oneRepMax
        )
    }

    /**
     * Processes the data to compute max repetition chart entries.
     *
     * @param currentState The current state of max repetitions.
     * @param data The exercise history data.
     * @return An updated state for max repetitions.
     */
    private fun processMaxRep(
        currentState: LineChartData,
        data: List<ExerciseHistoryInfo>,
    ): LineChartData {
        val maxRep = data.flatMap { sets ->
            sets.sets.map {
                Entry(
                    sets.date.dayOfMonth.toFloat(),
                    ((it.secondMetric ?: 0).toDouble() * (it.firstMetric ?: 0.0)).toFloat()
                )
            }
        }.sortedBy { it.x }
        return currentState.copy(
            maxValue = maxRep.maxOfOrNull { it.y } ?: 0f,
            minValue = maxRep.minOfOrNull { it.y } ?: 0f,
            list = maxRep
        )
    }
}