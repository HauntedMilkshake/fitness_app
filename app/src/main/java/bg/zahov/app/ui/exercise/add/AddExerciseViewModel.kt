package bg.zahov.app.ui.exercise.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterItem
import bg.zahov.app.data.model.state.AddExerciseEventState
import bg.zahov.app.data.model.state.AddExerciseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add Exercise screen. Handles the UI state, user interactions, and business logic
 * for adding a new exercise.
 *
 * @property repo The `WorkoutProvider` repository used to manage workout-related data.
 */
class AddExerciseViewModel(
    private val repo: WorkoutProvider = Inject.workoutProvider,
) : ViewModel() {
    //A private mutable state flow that holds the current UI state for adding an exercise.
    private val _addExerciseData = MutableStateFlow(AddExerciseData())

    /**
     * Publicly exposed immutable state flow that represents the UI state for adding an exercise.
     */
    val addExerciseData: StateFlow<AddExerciseData> = _addExerciseData

    /**
     * Updates the exercise name in the UI state.
     *
     * @param name The new exercise name to set.
     */
    fun onNameChange(name: String) {
        _addExerciseData.update { old ->
            old.copy(name = name)
        }
    }

    /**
     * Updates the current UI event state.
     *
     * @param event The new event state to transition to.
     */
    fun changeEvent(event: AddExerciseEventState) {
        _addExerciseData.update { old -> old.copy(uiAddExerciseEventState = event) }
    }

    /**
     * Attempts to add a new exercise. Validates the user input and, if valid,
     * adds the exercise to the repository. If the operation is successful,
     * navigates back to the previous screen.
     */
    fun addExercise() {
        viewModelScope.launch {
            getSelectedBodyPart()?.let { bodyPart ->
                getSelectedCategory()?.let { category ->
                    repo.addTemplateExercise(
                        Exercise(
                            _addExerciseData.value.name,
                            bodyPart = bodyPart,
                            category = category,
                            true
                        )
                    )
                    changeEvent(AddExerciseEventState.NavigateBack)
                }
            }
        }
    }

    /**
     * Checks whether the "Add Exercise" button should be enabled based on
     * whether a name, body part, and category have been selected.
     *
     * @return `true` if all required fields are filled; otherwise `false`.
     */
    fun checkButtonAvailability() =
        (getSelectedBodyPart() == null || getSelectedCategory() == null || _addExerciseData.value.name.isBlank()).not()

    /**
     * Retrieves the selected body part filter from the list of filters.
     *
     * @return The selected `BodyPart`, or `null` if none is selected.
     */
    fun getSelectedBodyPart(): BodyPart? {
        return (_addExerciseData.value.bodyPartFilters
            .firstOrNull { it.selected && it.filter is Filter.BodyPartFilter }
            ?.filter as? Filter.BodyPartFilter)?.bodyPart
    }

    /**
     * Retrieves the selected category filter from the list of filters.
     *
     * @return The selected `Category`, or `null` if none is selected.
     */
    fun getSelectedCategory(): Category? {
        return (_addExerciseData.value.categoryFilters
            .firstOrNull { it.selected && it.filter is Filter.CategoryFilter }
            ?.filter as? Filter.CategoryFilter)?.category
    }

    /**
     * Updates the selected category filter in the UI state. Marks the specified filter as selected
     * and unselects all other filters.
     *
     * @param filter The `FilterItem` to mark as selected.
     */
    fun onCategoryFilterChange(filter: FilterItem) {
        _addExerciseData.update { old ->
            old.copy(
                categoryFilters = old.categoryFilters.map { item ->
                    item.copy(selected = (item.name == filter.name))
                }
            )
        }
    }

    /**
     * Updates the selected body part filter in the UI state. Marks the specified filter as selected
     * and unselects all other filters.
     *
     * @param filter The `FilterItem` to mark as selected.
     */
    fun onBodyPartFilterChange(filter: FilterItem) {
        _addExerciseData.update { old ->
            old.copy(
                bodyPartFilters = old.bodyPartFilters.map { item ->
                    item.copy(selected = (item.name == filter.name))
                }
            )
        }
    }
}