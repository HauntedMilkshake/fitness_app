package bg.zahov.app.ui.history.info

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


data class ExerciseDetails(
    val exerciseName: String,
    val sets: List<String>,
    val oneRepMaxes: List<String>,
)

data class HistoryInfoWorkout(
    val id: String = "",
    val workoutName: String = "",
    val workoutDate: String = "",
    val duration: String = "",
    val volume: String = "",
    val prs: String = "",
    val exercisesInfo: List<ExerciseDetails> = listOf(),
)

class HistoryInfoViewModel(
    private val workoutStateProvider: WorkoutStateManager = Inject.workoutState,
    private val workoutProvider: WorkoutProvider = Inject.workoutProvider,
    private val toastManager: ToastManager = ToastManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryInfoWorkout())
    val uiState: StateFlow<HistoryInfoWorkout> = _uiState

    private var workout: Workout? = null
    private var templates: MutableList<Workout> = mutableListOf()
    private var currentWorkoutState: WorkoutState? = null

    init {
        viewModelScope.launch {
            launch {
                workoutProvider.getTemplateWorkouts().collect {
                    templates = it.toMutableList()
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
                            exercisesInfo = it.exercisesInfo
                        )
                    }
                }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            workout?.let {
                workoutProvider.deleteWorkout(it)
            }
        }
    }

    fun saveAsTemplate() {
        if (templates.any { it.id == workout?.id }) {
            toastManager.showToast(R.string.workout_exists_toast)
            return
        }

        viewModelScope.launch {
            workout?.copy(isTemplate = true, duration = 0L, volume = 0.0, personalRecords = 0)
                ?.let {
                    workoutProvider.addTemplateWorkout(it)
                    templates.add(it)
                }
        }
    }

    fun performAgain() {
        viewModelScope.launch {
            when (currentWorkoutState) {
                WorkoutState.MINIMIZED, WorkoutState.ACTIVE -> {
                    toastManager.showToast(R.string.toast_couldnt_start_workout)
                }

                WorkoutState.INACTIVE -> workoutStateProvider.startWorkout(workout)
                null -> { /* no-op */
                }
            }
        }
    }
}
