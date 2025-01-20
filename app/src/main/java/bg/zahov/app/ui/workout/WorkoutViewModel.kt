package bg.zahov.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.data.provider.WorkoutStateManager
import bg.zahov.app.util.generateRandomId
import bg.zahov.app.util.hashString
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale
import java.util.Random

/**
 * Represents the UI state of a workout session.
 *
 * @property workoutName The name of the workout.
 * @property workoutPrefix The time of day prefix for the workout, determined by the current hour
 * @property exercises A list of workout entries, which can be either exercises or sets.
 * @property note Additional notes for the workout session.
 * @property isFinished Indicates whether the workout session is complete.
 */
data class WorkoutUiState(
    val workoutName: String = "",
    val workoutPrefix: TimeOfDay,
    val exercises: List<WorkoutEntry> = listOf(),
    val note: String = "",
    val isFinished: Boolean = false,
)

/**
 * A sealed class representing a workout entry, which can be either an exercise or a set
 *
 * A list of workoutEntry would look something like:
 * List<WorkoutEntry> { Exercise1, Set1ForExercise1, Set2ForExercise1, Exercise2, ... etc }
 * where the arrangement of items is important
 */
sealed class WorkoutEntry {

    /**
     * A data class representing an exercise in the workout.
     *
     * @property id A unique identifier for the exercise entry, default is generated using [generateRandomId].
     * @property name The name of the exercise.
     * @property firstInputColumnVisibility Determines whether the first input column for the exercise is visible. Default is true.
     * As of now it is not used however for future purposes I decided to leave it here
     * @property note An optional note related to the exercise. Default is an empty string.
     * @property bodyPart The body part targeted by the exercise.
     * @property category The category of the exercise (e.g., strength, endurance, etc.).
     */
    data class ExerciseEntry(
        val id: String = generateRandomId(),
        val name: String,
        val firstInputColumnVisibility: Boolean = true,
        val note: String = "",
        val bodyPart: BodyPart,
        val category: Category,
    ) : WorkoutEntry()

    /**
     * A data class representing a set in the exercise.
     *
     * @property id A unique identifier for the set entry, default is generated using [generateRandomId].
     * @property setType The type of set (e.g., default, warm-up, etc.). Default is [SetType.DEFAULT].
     * @property firstInputFieldVisibility Determines whether the first input field for the set is visible. Default is true.
     * @property setNumber The number of the set (e.g., 1, 2, 3...).
     * @property previousResults The results from the previous set, default is "-//-" indicating no prior results.
     * @property set The specific set details (e.g., weight, repetitions, etc.).
     * @property setCompleted Indicates whether the set has been completed. Default is false.
     */
    data class SetEntry(
        val id: String = generateRandomId(),
        val setType: SetType = SetType.DEFAULT,
        val firstInputFieldVisibility: Boolean = true,
        val setNumber: String,
        val previousResults: String = "-//-",
        val set: Sets,
        val setCompleted: Boolean = false,
    ) : WorkoutEntry()
}

/**
 * @property exercises a map of the exercises from the current session and their names as keys
 * @property volume amount of weight lifted during the current session
 * @property prs amount of personal records made during the current session
 */
data class ExerciseSummary(
    val exercises: LinkedHashMap<String, Exercise>,
    val volume: Double,
    val prs: Int,
)

/**
 * ViewModel responsible for managing the state and logic of a workout session.
 *
 * @property workoutStateManager Manages the state of the workout session.
 * @property repo Provides workout data and operations related to workouts.
 * @property addExerciseToWorkoutProvider Handles adding exercises to the current workout session.
 * @property restTimerProvider Manages timers for rest periods during the workout.
 * @property toastManager Manages the display of toast messages for user feedback.
 */
class WorkoutViewModel(
    private val workoutStateManager: WorkoutStateManager = Inject.workoutState,
    private val repo: WorkoutProvider = Inject.workoutProvider,
    private val addExerciseToWorkoutProvider: AddExerciseToWorkoutProvider = Inject.workoutAddedExerciseProvider,
    private val restTimerProvider: RestTimerProvider = Inject.restTimerProvider,
    private val toastManager: ToastManager = ToastManager,
) : ViewModel() {

    /**
     * A private mutable state flow that represents the UI state of the workout.
     */
    private val _uiState =
        MutableStateFlow<WorkoutUiState>(WorkoutUiState(workoutPrefix = calculateWorkoutPrefix()))

    /**
     * A public read-only state flow exposing the current workout UI state to observers.
     */
    val uiState: StateFlow<WorkoutUiState> = _uiState

    /**
     * The index of the exercise to replace in the current workout, if applicable.
     */
    private var exerciseToReplaceIndex: Int? = null

    /**
     * A list of template exercises used to lookup previous results
     */
    private var templateExercises = listOf<Exercise>()

    /**
     * Received by arguments. The id of the template workout of which
     * this workout will be based on
     */
    private var workoutId: String? = null

    /**
     * The date and time when the workout session was created.
     */
    private val workoutDate: LocalDateTime = LocalDateTime.now()

    /**
     * Time duration of the workout.
     */
    private var elapsedTime: Long = 0L

    /**
     * Initializes the `WorkoutViewModel` by setting up multiple asynchronous data flows to manage the UI state
     */

    init {
        checkForTemplateWorkout()
        observeTimer()
        addSelectedExercises()
        getTemplateExercises()
        observeShouldFinish()
    }

    /**
     * Observes the workout state from the [workoutStateManager] and performs actions based on the current state.
     *
     * - When the state is [WorkoutState.INACTIVE], the workout is finished by calling [finishWorkout].
     *
     */
    private fun observeShouldFinish() {
        viewModelScope.launch {
            repo.shouldFinish.collect { shouldFinish ->
                if (shouldFinish) {
                    finishWorkout()
                }
            }
        }
    }

    /**
     * Determines the current time of day and returns the corresponding [TimeOfDay] value.
     *
     * The function evaluates the current hour using [LocalDateTime.now] and maps it
     * to a specific time of day:
     * - 4 AM to 11 AM -> [TimeOfDay.MORNING]
     * - 12 PM to 4 PM -> [TimeOfDay.NOON]
     * - 5 PM to 8 PM -> [TimeOfDay.AFTERNOON]
     * - Any other hour -> [TimeOfDay.NIGHT]
     *
     * @return The appropriate [TimeOfDay] based on the current hour.
     */
    private fun calculateWorkoutPrefix(): TimeOfDay {
        return when (LocalDateTime.now().hour) {
            in 4..11 -> TimeOfDay.MORNING
            in 12..16 -> TimeOfDay.NOON
            in 17..20 -> TimeOfDay.AFTERNOON
            else -> TimeOfDay.NIGHT
        }
    }

    /**
     * Checks for a template workout and updates the UI state with its details if available.
     */
    private fun checkForTemplateWorkout() {
        viewModelScope.launch {
            workoutStateManager.template.collect {
                it?.let { workout ->
                    _uiState.update { old ->
                        val exercises = _uiState.value.exercises.toMutableList()
                        exercises.addAll(
                            workout.exercises.toWorkoutEntryList(
                                templateExercises
                            )
                        )

                        old.copy(
                            workoutName = workout.name,
                            exercises = exercises,
                            note = workout.note ?: ""
                        )
                    }
                }
            }
        }
    }

    /**
     * Observes the workout timer from `workoutStateManager` and updates the UI state with the current timer value.
     *
     * The timer value is converted to rest time format before updating the state.
     */
    private fun observeTimer() {
        viewModelScope.launch {
            workoutStateManager.timer.collect {
                elapsedTime = it
            }
        }
    }

    /**
     * Adds exercises selected from `addExerciseToWorkoutProvider` to the current workout's exercises.
     *
     * This function observes the selected exercises flow, appends them to the UI state's exercise list,
     * and resets the selected exercises in the provider.
     */
    private fun addSelectedExercises() {
        viewModelScope.launch {
            addExerciseToWorkoutProvider.selectedExercises.collect {
                _uiState.update { old ->
                    val exercisesToUpdate = _uiState.value.exercises.toMutableList()
                    exercisesToUpdate.addAll(it.toWorkoutEntryList(templateExercises))
                    old.copy(exercises = exercisesToUpdate)
                }
                addExerciseToWorkoutProvider.resetSelectedExercises()
            }
        }
    }

    /**
     * Fetches template exercises from the repository and updates the `templateExercises` property.
     */
    private fun getTemplateExercises() {
        viewModelScope.launch {
            repo.getTemplateExercises().collect {
                templateExercises = it
            }
        }
    }

    /**
     * A way to know which exercise to replace once we come back
     * with a selected exercise to replace
     *
     * @param itemPosition The index of the exercise to replace
     */
    fun replaceExercise(itemPosition: Int) {
        exerciseToReplaceIndex = itemPosition
    }

    /**
     * Removes the exercise for the current position
     * then iterates to remove its sets if there are any
     *
     * @param position The index of the exercise to remove
     */
    fun removeExercise(position: Int) {
        _uiState.update { old ->
            val newExercises = old.exercises.toMutableList()

            newExercises.removeAt(position)

            while (position < newExercises.size && newExercises[position] is WorkoutEntry.SetEntry) {
                newExercises.removeAt(position)
            }

            old.copy(exercises = newExercises)
        }
    }

    /**
     * Adds a new set to the workout at the specified position.
     *
     * @param position The index of the exercise in the workout list to which the set should be added.
     *
     * This function determines the correct position to insert the new set based on the workout structure
     * and ensures that the set is associated with the appropriate exercise. It handles edge cases, such as when
     * the exercise is the last entry in the list or when there is only one exercise in the workout. The new set
     * is configured using data from the corresponding template exercise if available.
     */
    fun addSet(position: Int) {
        _uiState.update { old ->

            var isFirstSet = false
            val exercises = old.exercises.toMutableList()
            val templateExercise =
                templateExercises.find { it.name == (exercises[position] as? WorkoutEntry.ExerciseEntry)?.name }

            if (exercises.size == 1 || position == exercises.size - 1) {
                insertSetAtIndex(
                    exercises,
                    position + 1,
                    position,
                    templateExercise?.category ?: Category.Barbell,
                    templateExercise
                )
                isFirstSet = true
            }

            if (!isFirstSet) {
                var index = position + 1

                while (index < exercises.size && exercises[index] !is WorkoutEntry.ExerciseEntry) {
                    index++
                }

                insertSetAtIndex(
                    exercises,
                    index,
                    position,
                    templateExercise?.category ?: Category.Barbell,
                    templateExercise
                )
            }

            old.copy(exercises = exercises)
        }
    }

    /**
     * Inserts a set entry into the list of workout exercises at a specific index.
     *
     * @param exercises The mutable list of workout entries (exercises and sets) to be updated.
     * @param insertIndex The index at which the new set entry should be inserted.
     * @param exercisePosition The position of the exercise to which the new set is associated.
     * @param exerciseCategory The category of the exercise (e.g., Barbell, Dumbbell).
     * @param templateExercise The template exercise used to prepopulate the set details, if available.
     *
     * This function creates a new set entry. If a corresponding template exercise exists and contains predefined sets,
     * it uses the template data to populate the set. Otherwise, it creates a default set entry.
     */
    private fun insertSetAtIndex(
        exercises: MutableList<WorkoutEntry>,
        insertIndex: Int,
        exercisePosition: Int,
        exerciseCategory: Category,
        templateExercise: Exercise?,
    ) {
        val setNumber = insertIndex - exercisePosition

        val setEntry = if (templateExercise != null && setNumber < templateExercise.sets.size) {
            templateExercise.sets[setNumber].toSetEntry(
                exerciseCategory = templateExercise.category,
                setNumber = setNumber - 1,
                previousResults = "${templateExercise.sets[setNumber].secondMetric} x ${templateExercise.sets[setNumber].secondMetric}"
            )
        } else {
            WorkoutEntry.SetEntry(
                firstInputFieldVisibility = exerciseCategory != Category.None,
                setNumber = setNumber.toString(),
                set = Sets(type = SetType.DEFAULT, firstMetric = 0.0, secondMetric = 0),
                previousResults = "-/-"
            )
        }

        exercises.add(insertIndex, setEntry)
    }

    /**
     * Changes the note for the workout
     *
     * @param note The new note for the workout
     */
    fun changeNote(note: String) {
        _uiState.update { old ->
            old.copy(note = note)
        }
    }

    /**
     * Removes the set in the specified position
     * and renumbers sets above/below it if needed
     *
     * @param position The index of the set to remove
     */
    fun removeSet(position: Int) {
        _uiState.update { old ->
            val exercises = old.exercises.toMutableList()
            exercises.removeAt(position)
            var index = position
            while (index < exercises.size && exercises[index] is WorkoutEntry.SetEntry) {
                (exercises[index] as? WorkoutEntry.SetEntry)?.let { setEntry ->
                    exercises[index] = setEntry.copy(
                        setNumber = (setEntry.setNumber.toInt() - 1).toString()
                    )
                }
                index++
            }
            old.copy(exercises = exercises)
        }
    }

    /**
     * Updates the weight of the appropriate set
     *
     * @param position The index of the set for whom we want to change the value
     * @param metric The value we want to update
     */
    fun onWeightChange(
        position: Int,
        metric: String,
    ) {
        _uiState.update { old ->
            val newEntries = old.exercises.toMutableList()
            if (position >= 0 && position < (old.exercises.size)) {
                (newEntries[position] as? WorkoutEntry.SetEntry)?.let { setEntry ->
                    val newSet = Sets(
                        type = setEntry.set.type,
                        firstMetric = metric.filterDoubleInput(),
                        secondMetric = setEntry.set.secondMetric
                    )
                    newEntries[position] = setEntry.copy(set = newSet)
                }
            }
            old.copy(exercises = newEntries)
        }

    }

    /**
     * Updates the weight of the appropriate set
     *
     * @param position The index of the set for whom we want to change the value
     * @param metric The value we want to update
     */
    fun onRepsChange(
        position: Int,
        metric: String,
    ) {
        _uiState.update { old ->
            val newEntries = old.exercises.toMutableList()
            if (position >= 0 && position < (old.exercises.size)) {
                (newEntries[position] as? WorkoutEntry.SetEntry)?.let { setEntry ->
                    val newSet = Sets(
                        type = setEntry.set.type,
                        firstMetric = setEntry.set.firstMetric,
                        secondMetric = metric.filterIntegerInput()
                    )
                    newEntries[position] = setEntry.copy(set = newSet)
                }
            }
            old.copy(exercises = newEntries)
        }

    }

    /**
     * changes the set type for the specified set
     *
     * @param itemPosition Index for the set
     * @param setType The new [SetType]
     */
    fun onSetTypeChanged(itemPosition: Int, setType: SetType) {
        _uiState.update { old ->
            val newEntries = old.exercises.toMutableList()

            (newEntries[itemPosition] as? WorkoutEntry.SetEntry)?.let { setEntry ->
                newEntries[itemPosition] =
                    setEntry.copy(setType = if (setType == setEntry.setType) SetType.DEFAULT else setType)
            }

            old.copy(exercises = newEntries)
        }
    }

    /**
     * minimizes workout by removing the fragment
     * and toggling the visibility of a view defined in the activity xml
     */
    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.minimizeWorkout()
        }
    }

    /**
     * simply cancels the workout
     */
    fun cancel() {
        viewModelScope.launch {
            restTimerProvider.stopRest()
            repo.clearWorkoutState()
            clearState()
            workoutStateManager.cancel()
        }
    }

    /**
     * clears the message so we can
     * show it again if needed
     */
    fun clearToast() {
        toastManager.clearToast()
    }

    /**
     * finishes the workout by ensuring we have no empty data
     * adds it to the database
     */
    fun finishWorkout() {
        if (canFinish()) {
            viewModelScope.launch {
                val exerciseSummary = getExerciseArrayAndPRs(_uiState.value.exercises)
                val exercises = exerciseSummary.exercises.values.toList()
                val prs = exerciseSummary.prs
                val volume = exerciseSummary.volume

                workoutId?.let {
                    repo.updateTemplateWorkout(it, workoutDate, exercises)
                }
                repo.addWorkoutToHistory(
                    Workout(
                        id = workoutId ?: hashString("${Random().nextInt(Int.MAX_VALUE)}"),
                        name = _uiState.value.workoutName,
                        date = workoutDate,
                        exercises = exercises,
                        note = _uiState.value.note,
                        duration = elapsedTime,
                        isTemplate = false,
                        personalRecords = prs,
                        volume = volume
                    )
                )

                addExerciseToWorkoutProvider.resetSelectedExercises()
                resetFinishTrigger()
                workoutStateManager.finishWorkout()
                repo.clearWorkoutState()
                clearState()
            }

            _uiState.update { old ->
                old.copy(isFinished = true)
            }
        }
    }

    private fun resetFinishTrigger() {
        repo.completeFinishAttempt()
    }

    /**
     * under some rules decides if the workout is eligible to be finished
     */
    private fun canFinish(): Boolean {
        if (_uiState.value.exercises.isEmpty()) {
            toastManager.showToast(R.string.no_exercises)
            return false
        }
        if (_uiState.value.exercises.all { entry -> entry is WorkoutEntry.ExerciseEntry }) {
            toastManager.showToast(R.string.no_sets)
            return false
        }
        if (_uiState.value.exercises.filterIsInstance<WorkoutEntry.SetEntry>().all {
                (it.set.secondMetric ?: 0) == 0 || (it.set.firstMetric
                    ?: 0.0) == 0.0
            }) {
            toastManager.showToast(R.string.empty_sets)
            return false
        }
        return true
    }


    /**
     * Processes a list of `WorkoutEntry` objects to organize exercises and calculate the total workout volume.
     *
     * This function separates the workout data into exercises and their respective sets, while maintaining the order
     * of insertion. It also calculates the total volume of the workout based on the sets' weight and repetitions.
     *
     * @param entries A list of `WorkoutEntry` objects representing exercises and sets in the workout.
     * @return [ExerciseSummary]
     *
     * ## Details:
     * - `WorkoutEntry.ExerciseEntry`: Initializes a new `Exercise` or retrieves an existing one by name. The exercise is stored in the map.
     * - `WorkoutEntry.SetEntry`: Adds the set to the most recently added exercise and updates the total volume.
     *   - If the set has valid weight and reps, it contributes to the workout volume (`volume += weight * reps`).
     *   - For `SetType.DEFAULT` or `SetType.FAILURE`, the best set is updated if the weight exceeds the current best.
     *
     * ### Constraints:
     * - The function assumes that sets (`WorkoutEntry.SetEntry`) are always added after an exercise (`WorkoutEntry.ExerciseEntry`).
     * - If the same exercise appears multiple times, its sets are grouped under the same `Exercise` object.
     *
     */
    private fun getExercisesFiltered(
        entries: List<WorkoutEntry>,
    ): ExerciseSummary {
        val exercises =
            LinkedHashMap<String, Exercise>()
        var volume = 0.0
        entries.map { entry ->
            when (entry) {
                is WorkoutEntry.ExerciseEntry -> {
                    // Adds a new exercise or retrieves the existing one by name.
                    exercises.getOrPut(entry.name) {
                        Exercise(
                            name = entry.name,
                            bodyPart = entry.bodyPart,
                            category = entry.category,
                            isTemplate = false,
                            note = entry.note
                        )
                    }
                }

                is WorkoutEntry.SetEntry -> {
                    // Adds the set to the most recent exercise and calculates its contribution to volume.
                    //when adding them in a list we eliminate the problem where if the user has performed an exercise twice
                    val reps = entry.set.secondMetric
                    val weight = entry.set.firstMetric
                    if (weight != null && weight != 0.0 && reps != null && reps != 0) {

                        exercises.entries.last().value.apply {
                            sets.add(entry.set)

                            volume += weight * reps
                            // Updates the best set if the current set is better for specific set types.
                            if (entry.set.type == SetType.DEFAULT || entry.set.type == SetType.FAILURE) {
                                if (weight > (bestSet.firstMetric ?: 0.0)) bestSet = entry.set
                            }
                        }
                    } else {

                    }
                }
            }
        }
        return ExerciseSummary(exercises = exercises, volume = volume, prs = 0)
    }

    /**
     * Processes a list of `WorkoutEntry` objects to extract exercises, calculate volume, and track personal records (PRs).
     *
     * This function organizes the workout data into exercises, calculates the workout volume, and optionally updates
     * the exercise data in the repository. It also compares the best sets of current exercises with template exercises
     * to determine if a new personal record (PR) is achieved.
     *
     * @param entries A list of `WorkoutEntry` objects representing exercises and sets in the workout.
     * @param updateExercises A boolean flag indicating whether to update the exercises in the repository.
     * If we save it to realm there is no need to update them in the firestore repository
     * @return [ExerciseSummary]:
     * ## Details:
     * 1. **Extract and Calculate**:
     *    - Calls `getExercisesFiltered` to extract exercises and calculate the total volume.
     * 2. **PR Comparison** (if `updateExercises` is `true`):
     *    - Compares the best set of each current exercise to its corresponding template exercise (if any).
     *    - Updates the `Exercise.bestSet` to retain the higher-performing set.
     *    - Increments the PR count (`prs`) if the current best set outperforms the template best set.
     *    - Marks template exercises as such (`isTemplate = true`).
     * 3. **Repository Update**:
     *    - Updates the exercise repository with the modified exercises.
     */
    private suspend fun getExerciseArrayAndPRs(
        entries: List<WorkoutEntry>,
        updateExercises: Boolean = true,
    ): ExerciseSummary {
        val filtered = getExercisesFiltered(entries)
        val exercises = filtered.exercises
        val volume = filtered.volume
        var prs = 0

        if (updateExercises) {

            templateExercises.forEach { previous ->
                val curr = exercises[previous.name]
                curr?.let { current ->
                    val currentBestSetResult = (current.bestSet.firstMetric
                        ?: 0.0) * (current.bestSet.secondMetric ?: 0)
                    val previousBestSetResult =
                        (previous.bestSet.firstMetric
                            ?: 0.0) * (previous.bestSet.secondMetric
                            ?: 0)

                    if (currentBestSetResult <= previousBestSetResult) {
                        exercises[previous.name]?.copy(bestSet = previous.bestSet)
                    } else {
                        prs++ // Increment PR count if the current best set surpasses the template best set.
                    }

                    exercises[previous.name]?.copy(isTemplate = true)
                }
            }

            repo.updateExercises(exercises.values.toList())
        }
        return ExerciseSummary(exercises = exercises, prs = prs, volume = volume)
    }

    /**
     * Updates the note for the corresponding exercise
     *
     * @param itemPosition The index for the exercise
     * @param text The new note
     */
    fun changeExerciseNote(itemPosition: Int, text: String) {
        _uiState.update { old ->
            val newExercises = old.exercises.toMutableList()

            (newExercises[itemPosition] as? WorkoutEntry.ExerciseEntry)?.copy(note = text)?.let {
                newExercises[itemPosition] = it
            }

            old.copy(exercises = newExercises)
        }
    }

    /**
     * Manually clearing the state because
     * the vm is tied to the activity's scope
     */
    private fun clearState() {
        _uiState.value = WorkoutUiState(workoutPrefix = TimeOfDay.EMPTY)
    }
}

/**
 * Converts an [Exercise] to a list of [WorkoutEntry] objects, including the exercise entry and its associated sets.
 *
 * This function maps an [Exercise] object to a list of [WorkoutEntry] objects, which include an [WorkoutEntry.ExerciseEntry] for
 * the exercise details and [WorkoutEntry.SetEntry] objects for each set associated with the exercise. If a matching template exercise
 * is found in the provided [templateExercise] list, the previous results for each set are filled in.
 *
 * @param templateExercise A list of [Exercise] objects that are used as a template to populate previous results for
 *                         sets if a matching exercise name is found.
 * @return A list of [WorkoutEntry] objects, including the exercise details and sets.
 */
fun Exercise.toWorkoutEntry(
    templateExercise: List<Exercise>,
): List<WorkoutEntry> {
    val foundExercise = templateExercise.find { it.name == this.name }
    val entries = mutableListOf<WorkoutEntry>(
        WorkoutEntry.ExerciseEntry(
            name = this.name,
            firstInputColumnVisibility = category != Category.None,
            note = this.note ?: "",
            bodyPart = this.bodyPart,
            category = this.category
        )
    )
    entries.addAll(this.sets.mapIndexed { i, set ->
        set.toSetEntry(
            this.category,
            i,
            if (foundExercise != null && i < foundExercise.sets.size) "${foundExercise.sets[i].secondMetric} x ${foundExercise.sets[i].firstMetric}" else "-/-"
        )
    })
    return entries
}

/**
 * Converts a [Sets] object to a [WorkoutEntry.SetEntry] object, including set details and previous results.
 *
 * This function converts a [Sets] object into a [WorkoutEntry.SetEntry], which includes the set type, input field
 * visibility based on the exercise category, set number, and previous results (if provided).
 *
 * @param exerciseCategory The category of the exercise, which is used to determine the visibility of the input field.
 * @param setNumber The index of the set, used to determine the set number in the output.
 * @param previousResults A string representing previous results for the set. Defaults to an empty string.
 * @return A [WorkoutEntry.SetEntry] object containing the set details.
 */
fun Sets.toSetEntry(
    exerciseCategory: Category,
    setNumber: Int,
    previousResults: String = "",
): WorkoutEntry.SetEntry {
    return WorkoutEntry.SetEntry(
        setType = this.type,
        firstInputFieldVisibility = exerciseCategory != Category.None,
        setNumber = (setNumber + 1).toString(),
        previousResults = previousResults,
        set = Sets(type = this.type, firstMetric = null, secondMetric = null),
        setCompleted = false
    )
}

/**
 * Converts a list of [Exercise] objects into a list of [WorkoutEntry] objects.
 *
 * This function maps each [Exercise] in the list to a list of [WorkoutEntry] objects using [toWorkoutEntry].
 *
 * @param templateExercises A list of [Exercise] objects used as templates for filling in previous results in each set.
 * @return A list of [WorkoutEntry] objects corresponding to the exercises in the input list.
 */
fun List<Exercise>.toWorkoutEntryList(
    templateExercises: List<Exercise>,
): List<WorkoutEntry> {
    return this.flatMap { it.toWorkoutEntry(templateExercises) }
}

/**
 * Converts a time duration in milliseconds (Long) to a formatted string representing hours, minutes, and seconds.
 *
 * This function takes a duration in milliseconds and converts it into a time string formatted as "hh:mm:ss".
 *
 * @return A formatted string representing the duration as hours, minutes, and seconds.
 */
fun Long.toRestTime(): String = String.format(
    Locale.getDefault(),
    "%02d:%02d:%02d",
    (this / (1000 * 60 * 60)) % 24,
    (this / (1000 * 60)) % 60,
    (this / 1000) % 60
)

/**
 * Filters a string to remove any non-numeric characters and returns the resulting integer.
 *
 * This function removes leading zeros, commas, and other invalid characters from a string and converts the cleaned
 * string to an integer. If the string cannot be converted, it defaults to 0.
 *
 * @return The integer value extracted from the string, or 0 if conversion fails.
 */
fun String.filterIntegerInput(): Int {
    if (this.startsWith('0') && this.length > 1) {
        this.dropWhile { it == '0' }
    }
    if (this.contains(",")) {
        this.drop(this.length - this.indexOf(","))
    }
    return this.toIntOrNull() ?: 0
}

/**
 * Filters a string to remove any non-numeric characters and returns the resulting double.
 *
 * This function removes leading zeros, commas, and other invalid characters from a string and converts the cleaned
 * string to a double. If the string cannot be converted, it defaults to 0.0.
 *
 * @return The double value extracted from the string, or 0.0 if conversion fails.
 */
fun String.filterDoubleInput(): Double {
    if (this.startsWith('0') || this.startsWith(',') && this.length > 1) {
        this.dropWhile { it == '0' || it == ',' }
    }
    return this.toDoubleOrNull() ?: 0.0
}

/**
 * Enum representing different times of the day, used for categorizing workouts based on the time they occur.
 *
 * Each enum value corresponds to a specific time of day (e.g., morning, afternoon, etc.) and has an associated
 * string resource for display purposes.
 *
 * @param stringResource The string resource representing the time of day.
 */
enum class TimeOfDay(val stringResource: Int) {
    MORNING(R.string.morning_workout), NOON(R.string.noon_workout), AFTERNOON(R.string.afternoon_workout), NIGHT(
        R.string.night_workout
    ),
    EMPTY(0)
}
