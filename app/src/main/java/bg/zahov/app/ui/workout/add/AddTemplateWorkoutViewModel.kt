package bg.zahov.app.ui.workout.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.ui.workout.WorkoutEntry
import bg.zahov.app.ui.workout.filterDoubleInput
import bg.zahov.app.ui.workout.toSetEntry
import bg.zahov.app.ui.workout.toWorkoutEntry
import bg.zahov.app.ui.workout.toWorkoutEntryList
import bg.zahov.app.util.filterIntegerInput
import bg.zahov.app.util.generateRandomId
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Represents the UI state for adding a template workout in the application.
 *
 * @property workoutName The name of the workout being added. Defaults to an empty string.
 * @property workoutDate The date and time of the workout. Must be provided when creating the instance.
 * @property exercises A list of exercises (or workout entries) included in the template workout. Defaults to an empty list.
 * @property note An optional note associated with the workout. Defaults to an empty string.
 * @property isAdded A flag indicating whether the workout has been successfully added. Defaults to `false`.
 */
data class AddTemplateWorkoutUiState(
    val workoutName: String = "",
    val workoutDate: LocalDateTime,
    val exercises: List<WorkoutEntry> = listOf(),
    val note: String = "",
    val isAdded: Boolean = false,
)

/**
 * ViewModel responsible for managing the state and logic for adding a template workout.
 *
 * @property workoutProvider Provides access to workout-related data and operations.
 * @property selectableExerciseProvider Handles operations for selecting exercises to add to the workout.
 * @property replaceableExerciseProvider Manages the replacement of existing exercises in the workout.
 * @property toastManager Manages toast notifications to provide feedback to the user.
 */
class AddTemplateWorkoutViewModel(
    private val workoutProvider: WorkoutProvider = Inject.workoutProvider,
    private val selectableExerciseProvider: AddExerciseToWorkoutProvider = Inject.workoutAddedExerciseProvider,
    private val replaceableExerciseProvider: ReplaceableExerciseProvider = Inject.replaceableExerciseProvider,
    private val toastManager: ToastManager = ToastManager,
) : ViewModel() {

    /**
     * The default date and time for the workout, initialized to the current date and time.
     */
    private var workoutDate = LocalDateTime.now()

    /**
     * The backing [MutableStateFlow] for the UI state, initialized with default values.
     * Provides a reactive data source for observing UI changes.
     */
    private val _uiState = MutableStateFlow<AddTemplateWorkoutUiState>(
        AddTemplateWorkoutUiState(workoutDate = workoutDate)
    )

    /**
     * The public [StateFlow] exposing the UI state.
     * Observers can collect this flow to react to UI state updates.
     */
    val uiState: StateFlow<AddTemplateWorkoutUiState> = _uiState

    /**
     * The index of the exercise to be replaced, if applicable.
     * Used when modifying an existing workout. Defaults to `null` when no replacement is needed.
     */
    private var exerciseToReplaceIndex: Int? = null

    /**
     * A list of all available templates of workouts.
     * Used to allow the user to select or modify a workout template.
     */
    private var templates: List<Workout> = listOf()

    /**
     * A flag indicating whether the current operation is editing an existing workout or adding a new one.
     * Defaults to `false`, meaning a new workout is being created.
     */
    private var edit = false

    /**
     * The ID of the workout being edited.
     * Used to identify and fetch the workout when in edit mode. Defaults to an empty string.
     */
    private var workoutIdToEdit: String = ""

    /**
     * A list of exercises associated with the selected workout template.
     * Populated when a template workout is selected.
     */
    private var templateExercises = listOf<Exercise>()

    init {
        viewModelScope.launch {
            observeTemplateExercises()
            observeTemplateWorkouts()
            observeSelectedExercises()
            observeExerciseToReplace()
        }
    }

    /**
     * Observes template exercises from the workout provider and updates the `templateExercises` field.
     */
    private fun observeTemplateExercises() {
        viewModelScope.launch {
            workoutProvider.getTemplateExercises().collect {
                templateExercises = it
            }
        }
    }

    /**
     * Observes template workouts from the workout provider and updates the `templates` field.
     */
    private fun observeTemplateWorkouts() {
        viewModelScope.launch {
            workoutProvider.getTemplateWorkouts().collect {
                templates = it
            }
        }
    }

    /**
     * Observes selected exercises from the selectable exercise provider.
     * Adds these exercises to the current UI state and resets the selected exercises.
     */
    private fun observeSelectedExercises() {
        viewModelScope.launch {
            selectableExerciseProvider.selectedExercises.collect {
                if (it.isNotEmpty()) {
                    _uiState.update { old ->
                        val exercisesToUpdate = _uiState.value.exercises.toMutableList()
                        exercisesToUpdate.addAll(it.toWorkoutEntryList(templateExercises))
                        old.copy(exercises = exercisesToUpdate)
                    }
                }
                selectableExerciseProvider.resetSelectedExercises()
            }
        }
    }

    /**
     * Observes the exercise to replace from the replaceable exercise provider.
     * Updates the UI state with the replaced exercise if it differs from the current entry.
     */
    private fun observeExerciseToReplace() {
        viewModelScope.launch {
            replaceableExerciseProvider.exerciseToReplace.collect {
                it?.let { replaced ->
                    val replacedEntry = replaced.toWorkoutEntry(templateExercises)
                    exerciseToReplaceIndex?.let { indexToReplace ->
                        if (_uiState.value.exercises[indexToReplace] != replacedEntry) {
                            _uiState.update { old ->
                                val captured = _uiState.value.exercises.toMutableList()
                                captured[indexToReplace] = replacedEntry.first()
                                old.copy(exercises = captured)
                            }
                            replaceableExerciseProvider.resetExerciseToReplace()
                        }
                    }
                }
            }
        }
    }

    /**
     * Initializes the workout editing process by setting the workout ID to edit and updating the UI state.
     *
     * This function checks if the `workoutIdToEdit` is empty before proceeding. If valid, it fetches the
     * workout corresponding to the given ID and updates the UI state with its details.
     *
     * @param editFlag Indicates whether the workout is being edited.
     * @param workoutId The ID of the workout to edit.
     */
    fun initEditWorkoutId(editFlag: Boolean, workoutId: String) {
        if (workoutIdToEdit.isEmpty()) {
            edit = editFlag
            workoutIdToEdit = workoutId

            viewModelScope.launch {
                workoutProvider.getTemplateWorkouts().first().let { workouts ->
                    workouts.find { it.id == workoutId }?.let { workout ->
                        _uiState.update { old ->
                            old.copy(
                                workoutName = workout.name,
                                workoutDate = workoutDate,
                                exercises = workout.exercises.toWorkoutEntryList(templateExercises)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Clears any active toast messages by invoking the toast manager's clear function.
     */
    fun clearToast() {
        toastManager.clearToast()
    }

    /**
     * Resets the list of selected exercises by invoking the selectable exercise provider's reset function.
     */
    fun resetSelectedExercises() {
        selectableExerciseProvider.resetSelectedExercises()
    }

    /**
     * Sets the index of the exercise to be replaced.
     *
     * @param itemPosition The position of the exercise in the list that should be replaced.
     */
    fun setReplaceableExercise(itemPosition: Int) {
        exerciseToReplaceIndex = itemPosition
    }

    /**
     * Checks whether a template workout can be added.
     *
     * Validates that the workout has exercises and a name. If not, it shows appropriate toast messages.
     *
     * @return `true` if a workout can be added; `false` otherwise.
     */
    private fun canAdd(): Boolean {
        if (_uiState.value.exercises.isEmpty()) {
            toastManager.showToast(R.string.no_exercises)
            return false
        }
        if (_uiState.value.workoutName.isEmpty()) {
            toastManager.showToast(R.string.no_workout_name_toast)
            return false
        }
        return true
    }

    /**
     * Adds a new template workout if the necessary conditions are met.
     *
     * This function checks if a workout can be added using `canAdd`. If valid, it creates a new workout
     * with the UI state's details and invokes the workout provider to add it as a template workout.
     * The UI state is then updated to reflect the addition, and the temporary state is cleared.
     */
    fun addTemplateWorkout() {
        if (canAdd()) {
            viewModelScope.launch {
                workoutProvider.addTemplateWorkout(
                    Workout(
                        id = if (edit) workoutIdToEdit else generateRandomId(),
                        name = _uiState.value.workoutName,
                        note = _uiState.value.note,
                        duration = 0L,
                        date = _uiState.value.workoutDate,
                        isTemplate = true,
                        exercises = _uiState.value.exercises.toExerciseList(),
                        volume = null,
                    )
                )
                _uiState.update { it.copy(isAdded = true) }
            }
            clear()
        }
    }

    /**
     * Clears temporary state by resetting the selected exercises.
     * This is invoked after adding a template workout.
     */
    private fun clear() {
        resetSelectedExercises()
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
     * Updates the workout name in the UI state.
     *
     * @param newName The new name for the workout.
     */
    fun onWorkoutNameChange(newName: String) {
        _uiState.update { old ->
            old.copy(workoutName = newName)
        }
    }

    /**
     * Updates the workout note in the UI state.
     *
     * @param newNote The new note for the workout.
     */
    fun onWorkoutNoteChange(newNote: String) {
        _uiState.update { old ->
            old.copy(note = newNote)
        }
    }
}


fun List<WorkoutEntry>.toExerciseList(): List<Exercise> {
    val exercises = mutableListOf<Exercise>()
    var currentExercisesIndex = 0
    this.forEachIndexed { index, entry ->
        when (entry) {
            is WorkoutEntry.ExerciseEntry -> {
                if (index != 0) currentExercisesIndex++
                exercises.add(
                    Exercise(
                        name = entry.name,
                        bodyPart = entry.bodyPart,
                        category = entry.category,
                        note = entry.note
                    )
                )
            }

            is WorkoutEntry.SetEntry -> {
                val reps = entry.set.firstMetric
                val weight = entry.set.secondMetric
                if (reps != null && reps != 0.0 && weight != null && weight != 0) {
                    exercises[currentExercisesIndex].sets.add(entry.set)
                }
            }
        }
    }
    return exercises
}
