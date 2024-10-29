package bg.zahov.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.timeToString
import bg.zahov.app.util.toFormattedString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @property workouts A list of past workouts to be displayed.
 */
data class HistoryUiState(
    val workouts: List<HistoryWorkout> = listOf()
)

/**
 * @property duration The duration of the workout in a string format (e.g., "00:30:00").
 * @property volume The total volume of weight lifted during the workout.
 * @property date The date when the workout was performed.
 * @property exercises exercises performed in the workout( 3 x exerciseName - example).
 * @property bestSets best sets for the workout(the ones where the most weight was lifted).
 * @property personalRecords number of personal records set during the workout.
 */
data class HistoryWorkout(
    val id: String,
    val name: String,
    val duration: String,
    val volume: String,
    val date: String,
    val exercises: String,
    val bestSets: String,
    val personalRecords: String
)

/**
 * This ViewModel handles the fetching of past workouts and updates the UI state accordingly.
 *
 * @property workoutProvider An instance of [WorkoutProvider] used to retrieve past workouts.
 * @property serviceError An instance of [ServiceErrorHandler] used for handling errors during data retrieval.
 */
class HistoryViewModel(
    private val workoutProvider: WorkoutProvider = Inject.workoutProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        viewModelScope.launch {
            workoutProvider.getPastWorkouts().collect {
                try {
                    _uiState.update { old ->
                        old.copy(it.map { workout -> workout.toHistoryWorkout() })
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.initiateCountdown()
                }
            }
        }
    }
}

/**
 * Converts a [Workout] object to a [HistoryWorkout] object.
 *
 * This extension function transforms a [Workout] instance into a [HistoryWorkout] instance by extracting
 * relevant data and formatting it appropriately.
 *
 * @return A [HistoryWorkout] instance populated with data from this [Workout].
 */
fun Workout.toHistoryWorkout(): HistoryWorkout {
    return HistoryWorkout(
        id = this.id,
        name = this.name,
        duration = this.duration?.timeToString() ?: "00:00:00",
        volume = "${this.volume ?: 0} kg",
        date = this.date.toFormattedString(),
        exercises = this.exercises.joinToString("\n") {
            "${if (it.sets.isNotEmpty()) "${it.sets.size} x " else ""}${it.name} "
        },
        bestSets = this.exercises.joinToString("\n") {
            "${it.bestSet.firstMetric ?: 0} x ${it.bestSet.secondMetric ?: 0}"
        },
        personalRecords = this.personalRecords.toString()
    )
}
