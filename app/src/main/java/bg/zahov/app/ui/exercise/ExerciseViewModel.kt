package bg.zahov.app.ui.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterWrapper
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.data.model.state.ExerciseFlag
import bg.zahov.app.data.model.state.ExerciseScreenData
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.FilterProvider
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.data.provider.SelectableExerciseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class that manages the state for exercise-related operations.
 *
 * @property repo Provides access to workout data.
 * @property selectableExerciseProvider Provides functionality for selecting exercises.
 * @property replaceableExerciseProvider Provides functionality for replacing exercises.
 * @property addExerciseToWorkoutProvider Provides functionality for adding exercises to workouts.
 * @property filterProvider Provides access to filter data.
 * @property serviceError Handles service-related errors.
 */
class ExerciseViewModel(
    private val repo: WorkoutProvider = Inject.workoutProvider,
    private val selectableExerciseProvider: SelectableExerciseProvider = Inject.selectedExerciseProvider,
    private val replaceableExerciseProvider: ReplaceableExerciseProvider = Inject.replaceableExerciseProvider,
    private val addExerciseToWorkoutProvider: AddExerciseToWorkoutProvider = Inject.workoutAddedExerciseProvider,
    private val filterProvider: FilterProvider = Inject.filterProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {
    /**
     * Internal mutable state flow representing the current state of the exercise screen.
     * Used to hold and update the UI state data including filters, exercises, loading status, etc.
     */
    private val _exerciseData = MutableStateFlow(ExerciseScreenData())

    /**
     * Public immutable view of the exercise screen state.
     * Exposes the UI state data as a read-only flow for UI components.
     */
    val exerciseData: StateFlow<ExerciseScreenData> = _exerciseData


    init {
        viewModelScope.launch {
            launch {
                filterProvider.filters.collect { filters ->
                    _exerciseData.update { old ->
                        old.copy(filters = filters, loading = true)
                    }
                    _exerciseData.update { old ->
                        old.copy(exercises = getFiltered(), loading = false)
                    }
                }
            }
            launch {
                try {
                    repo.getWrappedExercises().collect { exercises ->
                        _exerciseData.update { old ->
                            old.copy(
                                exercises = exercises,
                                loading = false
                            )
                        }
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.stopApplication()
                }
            }
        }
    }

    /**
     * Handles the click event on an exercise item, adjusting the selection state based on the current flag.
     *
     * - If the flag is `ExerciseFlag.Adding` or `ExerciseFlag.Selecting`, toggles the selection of the clicked exercise
     *   without altering the selection states of other exercises.
     * - If the flag is `ExerciseFlag.Replacing`, sets the clicked exercise as the only selected exercise,
     *   deselecting all others.
     *
     * @param position The position of the clicked exercise in the list.
     */
    fun onExerciseClicked(position: Int) {
        _exerciseData.update { old ->
            old.copy(
                exercises = old.exercises.mapIndexed { index, exercise ->
                    when (_exerciseData.value.flag) {
                        ExerciseFlag.Adding, ExerciseFlag.Selecting -> if (index == position) exercise.copy(
                            selected = exercise.selected.not()
                        ) else exercise

                        ExerciseFlag.Replacing -> exercise.copy(selected = index == position)
                        else -> exercise
                    }
                }
            )
        }
    }


    /**
     * Filters exercises based on the current filters and search string.
     *
     * @param filter The list of filters to apply.
     * @param exercises The list of exercises to filter.
     * @param searchString The search query to match against exercise names.
     * @return A filtered list of exercises that match the filters and search query.
     */
    private fun getFiltered(
        filter: List<FilterWrapper> = _exerciseData.value.filters,
        exercises: List<ExerciseData> = _exerciseData.value.exercises,
        searchString: String = _exerciseData.value.search
    ): List<ExerciseData> {
        return exercises.map { exercise ->
            if ((filter.isEmpty() ||
                        filter.any { filterWrapper ->
                            matchesFilter(exercise, filterWrapper.filter)
                        })
                && (searchString.isBlank() ||
                        exercise.name.contains(searchString, ignoreCase = true))
            ) {
                exercise.copy(toShow = true)
            } else {
                exercise.copy(toShow = false)
            }
        }
    }

    /**
     * Checks if the given exercise matches the specified filter.
     *
     * @param exercise The exercise to check against the filter.
     * @param filter The filter to check.
     * @return True if the exercise matches the filter; otherwise, false.
     */
    private fun matchesFilter(
        exercise: ExerciseData,
        filter: Filter
    ): Boolean {
        return when (filter) {
            is Filter.CategoryFilter -> {
                exercise.category == filter.category
            }

            is Filter.BodyPartFilter -> {
                exercise.bodyPart == filter.bodyPart
            }
        }
    }

    /**
     * Sets the clicked exercise in the repository based on the exercise name.
     *
     * @param position The position of the clicked exercise.
     */
    fun setClickedExercise(position: Int) {
        viewModelScope.launch {
            _exerciseData.value.exercises[position].let {
                repo.setClickedTemplateExercise(it)
            }
        }
    }

    /**=
     * Confirms the selected exercises based on the current flag state.
     * Depending on the flag, it either replaces, adds, or selects exercises
     * from the user's selection.
     */
    fun confirmSelectedExercises() {
        _exerciseData.value.exercises.filter { it.selected }.let {
            if (it.isNotEmpty()) {
                _exerciseData.update { old -> old.copy(loading = true) }
                when (_exerciseData.value.flag) {
                    ExerciseFlag.Replacing -> {
                        replaceSelectedExercise(it)
                    }

                    ExerciseFlag.Adding -> {
                        addSelectedExercises(it)
                    }

                    ExerciseFlag.Selecting -> {
                        selectSelectedExercises(it)
                    }

                    else -> {/* Unreachable code */
                    }
                }
                resetExerciseSelection()
            }
        }
    }

    /**
     * Replaces the currently selected exercise in the replaceableExerciseProvider with the first exercise
     * from the provided list. It updates the state to navigate back after replacement.
     *
     * @param selectedExercises A list of exercises selected by the user.
     */
    private fun replaceSelectedExercise(selectedExercises: List<ExerciseData>) {
        viewModelScope.launch {
            selectedExercises.first().let { selected ->
                repo.getExerciseByName(selected.name).collect { exercise ->
                    exercise?.let { replaceableExerciseProvider.updateExerciseToReplace(it) }
                    _exerciseData.update { old -> old.copy(navigateBack = true) }
                }
            }
        }
    }

    /**
     * Adds the selected exercises to the selectableExerciseProvider. After adding, it updates the state
     * to navigate back to the previous screen.
     *
     * @param selectedExercises A list of exercises selected by the user.
     */
    private fun selectSelectedExercises(selectedExercises: List<ExerciseData>) {
        viewModelScope.launch {
            repo.getExercisesByWrapper(selectedExercises).collect {
                selectableExerciseProvider.addExercises(it)
                _exerciseData.update { old -> old.copy(navigateBack = true) }
            }
        }
    }

    /**
     * Adds the selected exercises to the addExerciseToWorkoutProvider to include them in the workout.
     * After adding, it updates the state to navigate back to the previous screen.
     *
     * @param selectedExercises A list of exercises selected by the user.
     */
    private fun addSelectedExercises(selectedExercises: List<ExerciseData>) {
        viewModelScope.launch {
            repo.getExercisesByWrapper(selectedExercises).collect {
                addExerciseToWorkoutProvider.addExercises(it)
                _exerciseData.update { old -> old.copy(navigateBack = true) }
            }
        }
    }

    /**
     * Resets the current exercise selection state by updating the exercise flag
     * to default and clearing any selected exercises.
     */
    fun resetExerciseSelection() {
        _exerciseData.update { it.copy(flag = ExerciseFlag.Default) }
        selectableExerciseProvider.resetSelectedExercises()
    }

    /**
     * Updates the search query and filters the exercise list accordingly.
     *
     * @param search The new search query.
     */
    fun onSearchChange(search: String) {
        Log.d("changed", search)
        _exerciseData.update { old ->
            old.copy(search = search, loading = true)
        }
        _exerciseData.update { old ->
            old.copy(exercises = getFiltered(searchString = search), loading = false)
        }
    }

    /**
     * Updates the exercise action flag based on the provided parameters.
     *
     * @param addable Indicates if exercises can be added.
     * @param replaceable Indicates if exercises can be replaced.
     * @param selectable Indicates if exercises can be selected.
     */
    fun updateFlag(addable: Boolean, replaceable: Boolean, selectable: Boolean) {
        val flag = when {
            replaceable -> ExerciseFlag.Replacing
            addable -> ExerciseFlag.Adding
            selectable -> ExerciseFlag.Selecting
            else -> ExerciseFlag.Default
        }
        _exerciseData.update { old -> old.copy(flag = flag) }
    }

    /**
     * Updates the visibility of the dialog.
     *
     * @param showDialog Indicates whether to show the dialog.
     */
    fun updateShowDialog(showDialog: Boolean) {
        _exerciseData.update { old -> old.copy(showDialog = showDialog) }
    }

    /**
     * Removes a specified filter from the filter provider.
     *
     * @param filter The filter to remove.
     */
    fun removeFilter(filter: FilterWrapper) {
        viewModelScope.launch {
            filterProvider.updateFilter(filter)
        }
    }
}