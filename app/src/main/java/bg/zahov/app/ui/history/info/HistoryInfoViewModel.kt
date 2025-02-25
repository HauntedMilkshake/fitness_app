package bg.zahov.app.ui.history.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.WorkoutActions
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.data.provider.model.HistoryInfoWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the state and actions related to workout history details.
 *
 * @constructor Creates a HistoryInfoViewModel with the required dependencies.
 * @property workoutStateProvider Provides the current workout state.
 * @property workoutProvider Handles operations related to workouts, such as retrieving and modifying them.
 */
@HiltViewModel
class HistoryInfoViewModel @Inject constructor(
    private val workoutStateProvider: WorkoutStateManager,
    private val workoutProvider: WorkoutProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryInfoWorkout())
    val uiState: StateFlow<HistoryInfoWorkout> = _uiState

    private var templates: List<Workout> = listOf()

    private var pastWorkouts: List<Workout> = listOf()

    private var currentWorkoutState: WorkoutState? = null

    /**
     * Initializes the ViewModel by collecting template workouts, the current workout state,
     * and the selected workout from history.
     */
    init {
        observeTemplateWorkouts()
        observerHistoryWorkouts()
        observerClickedWorkout()
        observerSaveTrigger()
        observerSaveTrigger()
        observerDeleteTrigger()
        observeWorkoutState()
    }

    /**
     * Observes the state of the workout
     * @see workoutStateProvider
     */
    private fun observeWorkoutState() {
        viewModelScope.launch {
            workoutStateProvider.state.collect {
                currentWorkoutState = it
            }
        }
    }

    /**
     * Observes template workouts and updates the `templates` list when changes occur.
     * @see workoutProvider
     */
    private fun observeTemplateWorkouts() {
        viewModelScope.launch {
            workoutProvider.getTemplateWorkouts().collect {
                templates = it
            }
        }
    }

    /**
     * Observes historical workouts and updates the `pastWorkouts` list when changes occur.
     * @see workoutProvider
     */
    private fun observerHistoryWorkouts() {
        viewModelScope.launch {
            workoutProvider.getPastWorkouts().collect {
                pastWorkouts = it
            }
        }
    }

    /**
     * Observes the clicked workout and updates the UI state with its details.
     * @see workoutProvider
     */
    private fun observerClickedWorkout() {
        viewModelScope.launch {
            workoutProvider.clickedPastWorkout.collect {
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

    /**
     * Observes the save trigger and saves the current workout as a template if triggered.
     * @see workoutProvider
     */
    private fun observerSaveTrigger() {
        viewModelScope.launch {
            workoutProvider.shouldSaveAsTemplate.collect {
                if (it) {
                    saveAsTemplate()
                }
            }
        }
    }

    /**
     * Observes the delete trigger and deletes the current workout from history if triggered.
     * @see workoutProvider
     */
    private fun observerDeleteTrigger() {
        viewModelScope.launch {
            workoutProvider.shouldDeleteHistoryWorkout.collect {
                if (it) {
                    delete()
                }
            }
        }
    }

    private fun getCorrespondingWorkout(): Workout? {
        return pastWorkouts.find { it.id == _uiState.value.id }
    }

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
            } ?: run {
                /*TODO(change to snackbar  ( R.string.failed_to_delete_workout_toast ) ) */
            }
        }
    }

    /**
     * Saves the current workout as a template, if it does not already exist in the templates.
     * Displays a toast notification if the workout already exists as a template.
     */
    private fun saveAsTemplate() {
        viewModelScope.launch {
            if (templates.any { it.id == getCorrespondingWorkout()?.id }) {
                /*TODO(change to snackbar  ( R.string.workout_exists_toast ) ) */
            } else {
                getCorrespondingWorkout()?.copy(
                    isTemplate = true,
                    duration = 0L,
                    volume = 0.0,
                    personalRecords = 0
                )
                    ?.let {
                        workoutProvider.addTemplateWorkout(it)
                    }
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
                    /*TODO(change to snackbar  ( R.string.toast_couldnt_start_workout ) ) */
                }

                WorkoutState.INACTIVE -> workoutStateProvider.startWorkout(
                    getCorrespondingWorkout()
                )

                null -> { /* no-op */
                }
            }
        }
    }
}
