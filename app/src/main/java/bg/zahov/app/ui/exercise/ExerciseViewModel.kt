package bg.zahov.app.ui.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import bg.zahov.app.ExerciseArgs.ADD_EXERCISE_ARG
import bg.zahov.app.ExerciseArgs.REPLACE_EXERCISE_ARG
import bg.zahov.app.ExerciseArgs.SELECT_EXERCISE_ARG
import bg.zahov.app.Exercises
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ExercisesTopBarHandler
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterItem
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.data.model.state.ExerciseFlag
import bg.zahov.app.data.model.state.ExerciseScreenData
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.FilterProvider
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.data.provider.SelectableExerciseProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: WorkoutProvider,
    private val selectableExerciseProvider: SelectableExerciseProvider,
    private val replaceableExerciseProvider: ReplaceableExerciseProvider,
    private val addExerciseToWorkoutProvider: AddExerciseToWorkoutProvider,
    private val filterProvider: FilterProvider,
    private val serviceError: ServiceErrorHandler,
    private val exerciseTopBarManager: ExercisesTopBarHandler,
) : ViewModel() {

    private val exerciseState = savedStateHandle.toRoute<Exercises>().state

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
        updateFlag(exerciseState)
        viewModelScope.launch {
            launch {
                filterProvider.filters.collect { filters ->
                    _exerciseData.update { old ->
                        old.copy(filters = filters, loading = true)
                    }
                    getFiltered()
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
            launch {
                exerciseTopBarManager.openDialog.collect {
                    _exerciseData.update { old -> old.copy(showDialog = it) }
                }
            }
            launch {
                exerciseTopBarManager.search.collect {
                    _exerciseData.update { old ->
                        old.copy(search = it)
                    }
                    getFiltered()
                }
            }
        }
    }

    /**
     * Handles the event when an exercise is clicked.
     *
     * Depending on the current exercise flag, the function either navigates to the exercise
     * information screen or sets the selected exercise.
     *
     * - If the flag is `ExerciseFlag.Default`, it updates the exercise information screen
     *   and sets the navigation flag to `true`, indicating that the app should navigate to the exercise details.
     * - Otherwise, it sets the clicked exercise as the selected exercise.
     *
     * @param position The position of the clicked exercise in the list.
     */
    fun onExerciseClicked(position: Int) {
        if (_exerciseData.value.flag == ExerciseFlag.Default) {
            updateExerciseInfoScreen(position)
            _exerciseData.update { old -> old.copy(navigateInfo = true) }
        } else {
            setSelectedExercise(position)
        }
    }

    /**
     * Resets the state after performing the navigation callback so as to prevent
     * bugs(e.g. when the user goes back into the screen and the state hasn't reset so he gets
     * instantly navigated away from it)
     */
    fun resetNavigationState() {
        _exerciseData.update { old ->
            old.copy(navigateInfo = false)
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
    private fun setSelectedExercise(position: Int) {
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
     * Sets the clicked exercise in the repository based on the exercise position in the list.
     *
     * @param position The position of the clicked exercise.
     */
    private fun updateExerciseInfoScreen(position: Int) {
        viewModelScope.launch {
            _exerciseData.value.exercises[position].let {
                repo.setClickedTemplateExercise(it)
            }
        }
    }

    /**
     * Updates the exercise list to show only the exercises that match the current filters and search string.
     */
    private fun getFiltered() {
        _exerciseData.update {
            it.copy(
                exercises = it.exercises.map { exercise ->
                    val matchesFilter = matchesAnyFilter(exercise)
                    val matchesSearch = matchesSearch(exercise)
                    exercise.copy(toShow = matchesFilter && matchesSearch)
                },
                loading = false
            )
        }
    }

    /**
     * Checks if an exercise matches the search string.
     *
     * @param exercise The exercise to test.
     * @param searchString The search query to match against the exercise name. Defaults to the current search string in the state.
     * @return `true` if the search string is blank or the exercise name contains the search string (case-insensitive).
     */
    private fun matchesSearch(
        exercise: ExerciseData,
        searchString: String = _exerciseData.value.search,
    ): Boolean {
        return searchString.isBlank() || exercise.name.contains(searchString, ignoreCase = true)
    }

    /**
     * Determines whether an exercise matches any of the filters provided.
     *
     * The function checks both category and body part filters. If no filters are selected,
     * the function defaults to matching all exercises (i.e., no filtering is applied).
     *
     * @param exercise The exercise data to be checked.
     * @param filterList The list of filters to apply. Defaults to the current filters.
     * @return `true` if the exercise matches any of the filters, or if no filters are selected; `false` otherwise.
     */
    private fun matchesAnyFilter(
        exercise: ExerciseData,
        filterList: List<FilterItem> = _exerciseData.value.filters,
    ): Boolean {
        val categoryFilters = filterList.filter { it.filter is Filter.CategoryFilter }
        val bodyPartFilters = filterList.filter { it.filter is Filter.BodyPartFilter }

        val categoryMatch = categoryFilters.isEmpty() || categoryFilters.any { filterItem ->
            (filterItem.filter as Filter.CategoryFilter).category == exercise.category
        }

        val bodyPartMatch = bodyPartFilters.isEmpty() || bodyPartFilters.any { filterItem ->
            (filterItem.filter as Filter.BodyPartFilter).bodyPart == exercise.bodyPart
        }

        return categoryMatch && bodyPartMatch
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
            repo.getExercisesByNames(selectedExercises.map { it.name }).collect {
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
            repo.getExercisesByNames(selectedExercises.map { it.name }).collect {
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
        _exerciseData.update {
            it.copy(
                flag = ExerciseFlag.Default,
                navigateInfo = false,
                navigateBack = false
            )
        }
        selectableExerciseProvider.resetSelectedExercises()
    }

    /**
     * Updates the search query and filters the exercise list accordingly.
     *
     * @param search The new search query.
     */
    fun onSearchChange(search: String) {
        _exerciseData.update { old ->
            old.copy(search = search, loading = true)
        }
        getFiltered()
    }

    /**
     * Updates the exercise action flag based on the provided parameter.
     *
     * @param state A string mapped to a state.
     */
    fun updateFlag(state: String?) {
        _exerciseData.update { old ->
            old.copy(
                flag = when (state) {
                    REPLACE_EXERCISE_ARG -> ExerciseFlag.Replacing
                    ADD_EXERCISE_ARG -> ExerciseFlag.Adding
                    SELECT_EXERCISE_ARG -> ExerciseFlag.Selecting
                    else -> ExerciseFlag.Default
                }
            )
        }
    }

    /**
     * Updates the visibility value of the dialog in the exerciseTopBarManager .
     *
     * @param showDialog Indicates whether to show the dialog.
     */
    fun updateShowDialog(showDialog: Boolean) {
        viewModelScope.launch {
            exerciseTopBarManager.changeOpenDialog(showDialog)
        }
    }

    /**
     * Removes a specified filter from the filter provider.
     *
     * @param filter The filter to remove.
     */
    fun removeFilter(filter: FilterItem) {
        viewModelScope.launch {
            filterProvider.updateFilter(filter)
        }
    }
}