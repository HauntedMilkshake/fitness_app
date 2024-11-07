package bg.zahov.app.ui.workout.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.data.provider.WorkoutStateManager
import bg.zahov.app.util.generateRandomId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Represents the UI state for starting a workout.
 *
 * @property workouts A list of template workouts the user has created.
 * @property notifyUser An optional message to notify the user.
 */
data class StartWorkoutUiState(
    val workouts: List<StartWorkout> = listOf(),
    val notifyUser: String? = null
)

/**
 * Template workout
 *
 * @property id The unique identifier for the workout.
 * @property name The name of the workout.
 * @property date The date when the workout is created.
 * @property exercises A list of exercise names included in the workout.
 */
data class StartWorkout(
    val id: String,
    val name: String,
    val date: String,
    val exercises: List<String>,
    val note: String = "",
    val personalRecords: String = "0"
)

/**
 * ViewModel responsible for managing the state of the start workout screen.
 *
 * @param repo The provider responsible for fetching workout templates.
 * @param workoutState The state manager responsible for tracking the current workout state.
 * @param serviceError The handler for managing service errors.
 */
class StartWorkoutViewModel(
    private val repo: WorkoutProvider = Inject.workoutProvider,
    private val workoutState: WorkoutStateManager = Inject.workoutState,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
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
     * used for mapping to [Workout] because otherwise we lose information for set types
     */
    private var exerciseTemplates: List<Exercise> = listOf()

    /**
     * collect template workouts, state and exercises which are to be used for mapping
     */
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
                    repo.getTemplateExercises().collect { templates ->
                        exerciseTemplates = templates
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.initiateCountdown()
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
                workoutState.startWorkout(workout = workout?.toWorkout(exerciseTemplates))
            } else {
                showMessage("Cannot start a workout while one is active")
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
    fun addDuplicateTemplateWorkout(newWorkout: StartWorkout) {
        viewModelScope.launch {
            repo.addTemplateWorkout(
                Workout(
                    id = generateRandomId(),
                    name = if (newWorkout.name.contains("duplicate")) newWorkout.name else "${newWorkout.name} duplicate",
                    duration = 0,
                    volume = 0.0,
                    date = LocalDateTime.now(),
                    isTemplate = true,
                    exercises = newWorkout.toWorkout(exerciseTemplates).exercises
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
            repo.deleteTemplateWorkout(workout.toWorkout(exerciseTemplates))
        }
    }

    /**
     * Notifies the user that a message has been shown.
     */
    fun messageShown() {
        showMessage()
    }

    /**
     * Updates the UI state to show a message to the user.
    e.
     *
     * @param text The text to display to the user, or `null` if no specific message is given.
     */
    private fun showMessage(text: String? = null) {
        _uiState.update { old ->
            old.copy(notifyUser = text)
        }
    }
}

/**
 * Converts a string representing a date and time into a `LocalDateTime` object.
 *
 * This function takes a string formatted as "HH:mm, d MMMM" (e.g., "13:00, 5 September") and parses it into a
 * `LocalDateTime` object for the current year. This is useful when the input string does not include the year,
 * but it is required for creating a `LocalDateTime` object.
 *
 * @return A `LocalDateTime` object that corresponds to the parsed date and time, with the current year.
 */
fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(
        "${LocalDate.now().year} $this",
        DateTimeFormatter.ofPattern("yyyy HH:mm, d MMMM")
    )

/**
 * Converts a `StartWorkout` to a `Workout` object.
 *
 * This extension function maps the `StartWorkout` to a `Workout` object, converting each exercise in the workout to
 * an `Exercise` object. The function uses a regular expression to parse exercise names and sets. It looks for a
 * matching `Exercise` template from the provided list (`exerciseTemplates`) based on the exercise's name.
 *
 * @param exerciseTemplates A list of available exercise templates that are used to determine additional information
 * such as body part and category.
 * @return A `Workout` object created from the `StartWorkout`.
 */
fun StartWorkout.toWorkout(exerciseTemplates: List<Exercise>) = Workout(
    id = this.id,
    name = this.name,
    duration = 0,
    volume = 0.0,
    date = this.date.toLocalDateTime(),
    isTemplate = true,
    exercises = this.exercises.map { exercise ->
        val regex = """(\d+)\s*x\s*(.*)""".toRegex()
        val matchResult = regex.find(exercise.trim())

        val (setsCount, name) = if (matchResult != null) {
            val setsCount = matchResult.groupValues[1].toInt()
            val name = matchResult.groupValues[2]
            setsCount to name
        } else {
            1 to exercise.trim()
        }

        val exerciseTemplate = exerciseTemplates.find { it.name == name }

        Exercise(
            name = name,
            bodyPart = exerciseTemplate?.bodyPart ?: BodyPart.Other,
            category = exerciseTemplate?.category ?: Category.None,
            isTemplate = false,
            sets = MutableList(setsCount) { Sets(SetType.DEFAULT, null, null) },
        )
    },
    note = this.note,
    personalRecords = this.personalRecords.toIntOrNull() ?: 0
)