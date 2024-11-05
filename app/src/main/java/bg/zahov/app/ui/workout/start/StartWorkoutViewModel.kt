package bg.zahov.app.ui.workout.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.data.provider.WorkoutStateManager
import bg.zahov.app.util.generateRandomId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class StartWorkoutUiState(
    val workouts: List<StartWorkout> = listOf(),
    val notifyUser: String? = null
)

data class StartWorkout(
    val id: String,
    val name: String,
    val date: String,
    val exercises: List<String>
)

class StartWorkoutViewModel(
    private val repo: WorkoutProvider = Inject.workoutProvider,
    private val workoutState: WorkoutStateManager = Inject.workoutState,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow<StartWorkoutUiState>(StartWorkoutUiState())
    val uiState: StateFlow<StartWorkoutUiState> = _uiState

    private var templates: List<Workout> = listOf()
    private var currentWorkoutState: WorkoutState = WorkoutState.ACTIVE

    init {
        viewModelScope.launch {
            launch {
                try {
                    repo.getStartWorkouts().collect { workouts ->
                        _uiState.update { old ->
                            old.copy(workouts = workouts)
                        }
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.initiateCountdown()
                }
            }
            launch {
                try {
                    repo.getTemplateWorkouts().collect {
                        templates = it
                    }
                } catch (e: CriticalDataNullException) {

                }
            }
            launch {
                workoutState.state.collect {
                    currentWorkoutState = it
                }
            }
        }
    }

    fun startEmptyWorkout() {
        viewModelScope.launch {
            if (currentWorkoutState == WorkoutState.INACTIVE) {
                workoutState.startWorkout(workout = null)
            } else {
                showMessage("Cannot start a workout while one is active")
            }
        }
    }

    fun startWorkoutFromTemplate(workout: StartWorkout) {
        viewModelScope.launch {
            if (currentWorkoutState == WorkoutState.INACTIVE) {
                workoutState.startWorkout(workout = templates.find { it.id == workout.id })
            } else {
                showMessage("Cannot start a workout while one is active")
            }
        }
    }

    //TODO(Might need to rewrite it to work for firestore delete with id)
    fun deleteTemplateWorkout(workout: StartWorkout) {
        _uiState.update { old ->
            val newList = old.workouts.toMutableList().also { it.remove(workout) }
            old.copy(workouts = newList)
        }

        viewModelScope.launch {
            templates.find { it.id == workout.id }?.let {
                repo.deleteTemplateWorkout(it)
            }
        }
    }

    fun addDuplicateTemplateWorkout(newWorkout: StartWorkout) {
        _uiState.update { old ->
            val newList = old.workouts.toMutableList().also { it.add(newWorkout) }
            old.copy(newList)
        }

        viewModelScope.launch {
            templates.find { it.id == newWorkout.id }?.let {
                repo.addTemplateWorkout(
                    Workout(
                        id = generateRandomId(),
                        name = "${it.name} duplicate",
                        duration = 0,
                        volume = 0.0,
                        date = LocalDateTime.now(),
                        isTemplate = true,
                        exercises = it.exercises
                    )
                )
            }
        }
    }

    fun messageShown() {
        showMessage()
    }

    private fun showMessage(text: String? = null) {
        _uiState.update { old ->
            old.copy(notifyUser = text)
        }
    }
}
