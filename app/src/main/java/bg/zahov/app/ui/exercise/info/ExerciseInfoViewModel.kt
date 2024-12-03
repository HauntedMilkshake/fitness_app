package bg.zahov.app.ui.exercise.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.state.ExerciseHistoryData
import bg.zahov.app.data.model.state.ExerciseHistoryInfo
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing the UI state and business logic of the Exercise Info screen.
 *
 * @property workoutProvider The provider that fetches exercise history data. By default, it uses the injected instance from [Inject.workoutProvider].
 */
class ExerciseInfoViewModel(private val workoutProvider: WorkoutProvider = Inject.workoutProvider) :
    ViewModel() {
    //Internal state flow for managing UI data updates.
    private val _uiState = MutableStateFlow(ExerciseHistoryData())

    /**
     * Public state flow that exposes the UI state to observers.
     */
    val uiState: StateFlow<ExerciseHistoryData> = _uiState

    init {
        viewModelScope.launch {
            collectExerciseHistory()
        }
    }

    /**
     * Collects exercise history from the workout provider and processes it
     * to update the UI state with transformed data for chart entries.
     */
    private suspend fun collectExerciseHistory() {
        workoutProvider.getExerciseHistory().collect { data ->
            _uiState.update { old ->
                old.copy(
                    exerciseHistory = data,
                    maxVolume = processMaxVolume(data),
                    oneRepMaxEst = processOneRepMax(data),
                    maxRep = processMaxRep(data)
                )
            }
        }
    }

    /**
     * Processes the fetched data to calculate max volume chart entries.
     *
     * @param data List of exercise sets grouped by date.
     * @return A state object containing the max, min, and chart entries for max volume.
     */
    private fun processMaxVolume(data: List<ExerciseHistoryInfo>): LineChartData {
        val maxVolume = data.flatMap { sets ->
            sets.sets.map {
                Entry(
                    sets.date.dayOfMonth.toFloat(),
                    (it.secondMetric ?: 0).toFloat()
                )
            }
        }
        return LineChartData(
            maxValue = maxVolume.maxOfOrNull { it.y } ?: 0f,
            minValue = maxVolume.minOfOrNull { it.y } ?: 0f,
            list = maxVolume
        )
    }

    /**
     * Processes the fetched data to calculate one-rep max chart entries.
     *
     * @param data List of exercise sets grouped by date.
     * @return A state object containing the max, min, and chart entries for one-rep max.
     */
    private fun processOneRepMax(data: List<ExerciseHistoryInfo>): LineChartData {
        val oneRepMax = data.map { sets ->
            Entry(
                sets.date.dayOfMonth.toFloat(),
                sets.oneRepMaxes.toFloat()
            )
        }
        return LineChartData(
            maxValue = oneRepMax.maxOfOrNull { it.y } ?: 0f,
            minValue = oneRepMax.minOfOrNull { it.y } ?: 0f,
            list = oneRepMax
        )
    }

    /**
     * Processes the fetched data to calculate max repetition chart entries.
     *
     * @param data List of exercise sets grouped by date.
     * @return A state object containing the max, min, and chart entries for max repetition.
     */
    private fun processMaxRep(data: List<ExerciseHistoryInfo>): LineChartData {
        val maxRep = data.flatMap { sets ->
            sets.sets.map {
                Entry(
                    sets.date.dayOfMonth.toFloat(),
                    ((it.secondMetric ?: 0).toDouble() * (it.firstMetric ?: 0.0)).toFloat()
                )
            }
        }
        return LineChartData(
            maxValue = maxRep.maxOfOrNull { it.y } ?: 0f,
            minValue = maxRep.minOfOrNull { it.y } ?: 0f,
            list = maxRep
        )
    }

}