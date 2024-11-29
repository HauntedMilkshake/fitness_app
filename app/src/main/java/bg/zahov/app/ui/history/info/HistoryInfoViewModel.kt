package bg.zahov.app.ui.history.info

import android.util.Log
import bg.zahov.fitness.app.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.data.provider.WorkoutStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents detailed information about a specific exercise within a workout.
 *
 * @property exerciseName The name of the exercise.
 * @property sets A list of sets performed for the exercise, represented as strings.
 * @property oneRepMaxes A list of one-rep maxes for the exercise, represented as strings.
 */
data class ExerciseDetails(
    val exerciseName: String,
    val sets: List<String>,
    val oneRepMaxes: List<String>,
)

/**
 * Represents the detailed information of a workout history entry.
 *
 * @property id The unique identifier of the workout.
 * @property workoutName The name of the workout.
 * @property workoutDate The date when the workout was performed.
 * @property duration The duration of the workout, represented as a string.
 * @property volume The total volume lifted during the workout, represented as a string.
 * @property prs The personal records achieved during the workout, represented as a string.
 * @property exercisesInfo A list of detailed information for each exercise in the workout.
 * @property isDeleted indicates whether the workout has been deleted
 */
data class HistoryInfoWorkout(
    val id: String = "",
    val workoutName: String = "",
    val workoutDate: String = "",
    val duration: String = "",
    val volume: String = "",
    val prs: String = "",
    val exercisesInfo: List<ExerciseDetails> = listOf(),
    val isDeleted: Boolean = false,
)

/**
 * ViewModel for managing the state and actions related to workout history details.
 *
 * @constructor Creates a HistoryInfoViewModel with the required dependencies.
 * @property workoutStateProvider Provides the current workout state.
 * @property workoutProvider Handles operations related to workouts, such as retrieving and modifying them.
 * @property toastManager Manages the display of toast notifications.
 */
class HistoryInfoViewModel(
    private val workoutStateProvider: WorkoutStateManager = Inject.workoutState,
    private val workoutProvider: WorkoutProvider = Inject.workoutProvider,
    private val toastManager: ToastManager = ToastManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryInfoWorkout())
    val uiState: StateFlow<HistoryInfoWorkout> = _uiState

    private var templates: MutableList<Workout> = mutableListOf()
    private var pastWorkouts: MutableList<Workout> = mutableListOf()
    private var currentWorkoutState: WorkoutState? = null

    /**
     * Initializes the ViewModel by collecting template workouts, the current workout state,
     * and the selected workout from history.
     */
    init {
        viewModelScope.launch {
            launch {
                workoutProvider.getTemplateWorkouts().collect {
                    templates = it.toMutableList()
                }
            }
            launch {
                workoutProvider.getPastWorkouts().collect {
                    pastWorkouts = it.toMutableList()
                }
            }
            launch {
                workoutStateProvider.state.collect {
                    currentWorkoutState = it
                }
            }
            launch {
                workoutProvider.getClickedHistoryWorkout().collect {
                    _uiState.update { old ->
                        old.copy(
                            id = it.id,
                            workoutName = it.workoutName,
                            workoutDate = it.workoutDate,
                            duration = it.duration,
                            volume = it.volume,
                            prs = it.prs,
                            exercisesInfo = it.exercisesInfo,
                            isDeleted = false
                        )
                    }
                }
            }
        }
    }

    private fun getCorrespondingWorkout(): Workout? =
        pastWorkouts.find { it.id == _uiState.value.id }


    /**
     * Deletes the current workout from the history.
     */
    fun delete() {
        viewModelScope.launch {
            getCorrespondingWorkout()?.let {
                workoutProvider.deleteWorkout(it)
                _uiState.update { old ->
                    old.copy(isDeleted = true)
                }
            } ?: run { toastManager.showToast(R.string.failed_to_delete_workout_toast) }
        }
    }


    /**
     * Saves the current workout as a template, if it does not already exist in the templates.
     * Displays a toast notification if the workout already exists as a template.
     */
    fun saveAsTemplate() {

        if (templates.any { it.id == getCorrespondingWorkout()?.id }) {
            toastManager.showToast(R.string.workout_exists_toast)
            return
        }

        viewModelScope.launch {
            getCorrespondingWorkout()?.copy(
                isTemplate = true,
                duration = 0L,
                volume = 0.0,
                personalRecords = 0
            )
                ?.let {
                    workoutProvider.addTemplateWorkout(it)
                    templates.add(it)
                }
        }
    }

    /**
     * Attempts to start the current workout again. If a workout is already active or minimized,
     * displays a toast notification. Otherwise, starts the workout.
     */
    fun performAgain() {
        viewModelScope.launch {
            when (currentWorkoutState) {
                WorkoutState.MINIMIZED, WorkoutState.ACTIVE -> {
                    toastManager.showToast(R.string.toast_couldnt_start_workout)
                }

                WorkoutState.INACTIVE -> workoutStateProvider.startWorkout(getCorrespondingWorkout())
                null -> { /* no-op */
                }
            }
        }
    }
}
