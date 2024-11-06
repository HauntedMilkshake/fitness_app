package bg.zahov.app.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterWrapper
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.FilterProvider
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.data.provider.SelectableExerciseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Wrapper class for exercise data, including its name, associated body part, category,
 * and selection state.
 *
 * @property name The name of the exercise.
 * @property bodyPart The body part associated with the exercise.
 * @property category The category of the exercise.
 * @property selected Indicates whether the exercise is selected.
 */
data class ExercisesWrapper(
    val name: String,
    val bodyPart: BodyPart,
    val category: Category,
    var selected: Boolean = false
)

/**
 * Enum representing different states for exercise actions.
 */
enum class ExerciseFlag {
    Default,
    Selecting,
    Replacing,
    Adding;
}

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
    private val _exerciseData = MutableStateFlow(ExerciseData())
    val exerciseData: StateFlow<ExerciseData> = _exerciseData

    private var allExercises: List<ExercisesWrapper> = mutableListOf()

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
                        allExercises = exercises
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
     * Handles the click event on an exercise item. It changes what item/s are selected
     *
     * @param name The name of the clicked exercise in the list.
     */
    fun onExerciseClicked(name: String) {
        viewModelScope.launch {
            allExercises.find { it.name == name }?.let {
                it.selected = it.selected.not()
            }
        }
        _exerciseData.update { old ->
            val updatedExercises = old.exercises.map { exercise ->
                if (exercise.name == name) {
                    exercise.copy(
                        selected = allExercises.find { it.name == name }?.selected
                            ?: exercise.selected
                    )
                } else {
                    exercise
                }
            }
            old.copy(exercises = updatedExercises)
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
        exercises: List<ExercisesWrapper> = allExercises,
        searchString: String = _exerciseData.value.search
    ): List<ExercisesWrapper> {
        return exercises.filter { exercise ->
            val matchesFilters = filter.isEmpty() ||
                    filter.any { filterWrapper ->
                        matchesFilter(exercise, filterWrapper.filter)
                    }

            val matchesSearch = searchString.isBlank() ||
                    exercise.name.contains(searchString, ignoreCase = true)

            matchesFilters && matchesSearch
        }
    }

    /**
     * Checks if the given exercise matches the specified filter.
     *
     * @param exercise The exercise to check against the filter.
     * @param filter The filter to check.
     * @return True if the exercise matches the filter; otherwise, false.
     */
    private fun matchesFilter(exercise: ExercisesWrapper, filter: Filter): Boolean {
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
     * @param position The positioon of the clicked exercise.
     */
    fun setClickedExercise(name: String) {
        viewModelScope.launch {
            allExercises.find { it.name == name }?.let {
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
        val selectedExercises = _exerciseData.value.exercises.filter { it.selected }
            .mapNotNull { selected -> allExercises.find { it.name == selected.name } }
        viewModelScope.launch {
            when (_exerciseData.value.flag) {
                ExerciseFlag.Replacing -> replaceSelectedExercise(selectedExercises)
                ExerciseFlag.Adding -> addExerciseToWorkoutProvider.addExercises(
                    repo.getExercisesByWrapper(selectedExercises)
                )

                ExerciseFlag.Selecting -> selectableExerciseProvider.addExercises(
                    repo.getExercisesByWrapper(selectedExercises)
                )

                else -> { /* No action needed */
                }
            }
            resetExerciseSelection()
        }
    }

    /**
     * Replaces the currently selected exercise with the first selected exercise
     * from the provided list, if any.
     *
     * @param selectedExercises A list of exercises that have been selected.
     */
    private fun replaceSelectedExercise(selectedExercises: List<ExercisesWrapper>) {
        viewModelScope.launch {
            selectedExercises.firstOrNull()?.let { exercise ->
                repo.getExerciseByName(exercise.name)
                    ?.let { replaceableExerciseProvider.updateExerciseToReplace(it) }
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
        _exerciseData.update { old ->
            old.copy(
                search = search,
                exercises = getFiltered(searchString = search)
            )
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
            addable -> ExerciseFlag.Adding
            replaceable -> ExerciseFlag.Replacing
            selectable && replaceable.not() -> ExerciseFlag.Selecting
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