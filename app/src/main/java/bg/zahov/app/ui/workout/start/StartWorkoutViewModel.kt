package bg.zahov.app.ui.workout.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.data.provider.WorkoutStateManager
import bg.zahov.app.util.generateRandomId
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Represents the UI state for starting a workout.
 *
 * @property workouts A list of template workouts the user has created.
 */
data class StartWorkoutUiState(
    val workouts: List<StartWorkout> = listOf(),
)

/**
 * Represents a workout that is ready to be started or performed.
 *
 * @property id The unique identifier for the workout.
 * @property name The name of the workout.
 * @property date The date and time when the workout was created.
 * @property exercises A list of exercises included in the workout, represented by [StartWorkoutExercise].
 * @property note Additional notes or comments associated with the workout (optional).
 * @property personalRecords The count or description of personal records achieved during the workout, defaulting to "0".
 */
data class StartWorkout(
    val id: String,
    val name: String,
    val date: LocalDateTime,
    val exercises: List<StartWorkoutExercise>,
    val note: String = "",
    val personalRecords: String = "0",
)

/**
 * Represents an exercise that is part of a workout.
 *
 * @property name The name of the exercise (e.g., "Bench Press").
 * @property exercise A formatted string describing the exercise, often combining sets and the exercise name.
 * @property bodyPart The body part targeted by the exercise (e.g., Chest, Back, Legs).
 * @property category The category of the exercise, indicating the type of equipment or approach used
 * (e.g., Barbell, Dumbbell, Machine).
 */
data class StartWorkoutExercise(
    val name: String,
    val exercise: String,
    val bodyPart: BodyPart,
    val category: Category,
)

/**
 * ViewModel responsible for managing the state of the start workout screen.
 *
 * @param repo The provider responsible for fetching workout templates.
 * @param workoutState The state manager responsible for tracking the current workout state.
 * @param toastManager responsible for showing toasts to the user
 */
class StartWorkoutViewModel(
    private val repo: WorkoutProvider = Inject.workoutProvider,
    private val toastManager: ToastManager = ToastManager,
    private val workoutState: WorkoutStateManager = Inject.workoutState,
) : ViewModel() {

    /**
     * Holds the current UI state for starting a workout, including the list of available workouts and any user notifications.
     */
    private val _uiState = MutableStateFlow<StartWorkoutUiState>(StartWorkoutUiState())
    val uiState: StateFlow<StartWorkoutUiState> = _uiState

    /**
     * The current state of the workout [WorkoutState.ACTIVE] or [WorkoutState.INACTIVE] or [WorkoutState.MINIMIZED] .
     */
    private var currentWorkoutState: WorkoutState = WorkoutState.ACTIVE

    /**
     * collect template workouts, state and exercises which are to be used for mapping
     */
    init {
        viewModelScope.launch {
            launch {
                repo.getStartWorkouts().collect { workouts ->
                    _uiState.update { old ->
                        old.copy(workouts = workouts)
                    }
                }
            }
            launch {
                workoutState.state.collect {
                    currentWorkoutState = it
                }
            }
        }
    }

    /**
     * Starts an empty workout or one from template if one isn't active
     * else notifies the user
     */
    fun startWorkout(workout: StartWorkout? = null) {
        viewModelScope.launch {
            if (currentWorkoutState == WorkoutState.INACTIVE) {
                workoutState.startWorkout(workout = workout?.toWorkout())
            } else {
                toastManager.showToast(R.string.toast_couldnt_start_workout)
            }
        }
    }

    /**
     * Adds a duplicate template workout to the repository.
     *
     * This method creates a new `Workout` object based on the `StartWorkout` passed as input, appending the word "duplicate"
     * to the name if the workout's name does not already contain the word "duplicate". The newly created workout is then
     * added to the repository as a template workout.
     *
     * @param newWorkout The `StartWorkout` object that contains the details of the workout to duplicate.
     */
    fun addDuplicateTemplateWorkout(newWorkout: StartWorkout, duplicate: String) {
        viewModelScope.launch {
            repo.addTemplateWorkout(
                Workout(
                    id = generateRandomId(),
                    name = if (newWorkout.name.contains(duplicate)) newWorkout.name else "${newWorkout.name} duplicate",
                    duration = 0,
                    volume = 0.0,
                    date = LocalDateTime.now(),
                    isTemplate = true,
                    exercises = newWorkout.toWorkout().exercises
                )
            )
        }
    }

    /**
     * Deletes a template workout from the repository.
     *
     * This method deletes the workout (converted from the `StartWorkout` object) from the repository, effectively removing
     * the template workout.
     *
     * @param workout The `StartWorkout` object representing the workout to delete.
     */
    fun deleteTemplateWorkout(workout: StartWorkout) {
        viewModelScope.launch {
            repo.deleteTemplateWorkout(workout.toWorkout())
        }
    }
}

/**
 * Converts a `StartWorkout` to a `Workout` object.
 *
 * This extension function maps the `StartWorkout` to a `Workout` object, converting each exercise in the workout to
 * an `Exercise` object. The function uses a regular expression to parse exercise names and sets. It looks for a
 * matching `Exercise` template from the provided list (`exerciseTemplates`) based on the exercise's name.
 *
 * @return [Workout] object created from the `StartWorkout`.
 */
fun StartWorkout.toWorkout() = Workout(
    id = this.id,
    name = this.name,
    duration = 0,
    volume = 0.0,
    date = this.date,
    isTemplate = true,
    exercises = this.exercises.map { it.toExercise() },
    note = this.note,
    personalRecords = this.personalRecords.toIntOrNull() ?: 0
)

/**
 * Converts a [StartWorkoutExercise] object to an [Exercise] object.
 *
 * @receiver The [StartWorkoutExercise] instance being converted.
 * @return A new [Exercise] object with the following properties:
 * - `name`: The name of the exercise.
 * - `bodyPart`: The targeted body part of the exercise.
 * - `category`: The category of the exercise (e.g., Barbell, Dumbbell, Machine).
 */
fun StartWorkoutExercise.toExercise(): Exercise = Exercise(
    name = this.name,
    bodyPart = this.bodyPart,
    category = this.category
)
