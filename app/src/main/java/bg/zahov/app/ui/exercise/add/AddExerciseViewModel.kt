package bg.zahov.app.ui.exercise.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterItem
import bg.zahov.app.data.model.state.AddDialogExerciseData
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

    //A private mutable state flow that holds the current UI state for adding an exercise.
    private val _addDialogExerciseData = MutableStateFlow(AddDialogExerciseData())

    /**
     * Publicly exposed immutable state flow that represents the UI state for adding an exercise.
     */
    val addDialogExerciseData: StateFlow<AddDialogExerciseData> = _addDialogExerciseData


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
     * Updates the dialog state to show body part filters, category filters, or hide the dialog.
     *
     * @param showBodyPart A flag indicating whether to show the body part filters in the dialog. Defaults to `false`.
     * @param showCategory A flag indicating whether to show the category filters in the dialog. Defaults to `false`.
     *
     * If `showBodyPart` is `true`, the `toShow` list is populated with the body part filters, and the dialog is displayed.
     * If `showCategory` is `true`, the `toShow` list is populated with the category filters, and the dialog is displayed.
     * If both flags are `false`, the dialog is hidden.
     */
    fun showDialog(showBodyPart: Boolean = false, showCategory: Boolean = false) {
        if (showBodyPart)
            _addDialogExerciseData.update { old ->
                old.copy(
                    toShow = _addDialogExerciseData.value.bodyPartFilters,
                    showDialog = true
                )
            }
        else if (showCategory)
            _addDialogExerciseData.update { old ->
                old.copy(
                    toShow = _addDialogExerciseData.value.categoryFilters,
                    showDialog = true
                )
            }
        else
            _addDialogExerciseData.update { old -> old.copy(showDialog = false) }
    }

    /**
     * Checks whether the "Add Exercise" button should be enabled based on
     * whether a name, body part, and category have been selected.
     *
     * @return `true` if all required fields are filled; otherwise `false`.
     */
    fun checkButtonAvailability() =
        (_addExerciseData.value.selectedBodyPart == null || _addExerciseData.value.selectedCategory == null || _addExerciseData.value.name.isBlank()).not()


    /**
     * Attempts to add a new exercise. Validates the user input and, if valid,
     * adds the exercise to the repository. If the operation is successful,
     * navigates back to the previous screen.
     */
    fun addExercise() {
        viewModelScope.launch {
            _addExerciseData.value.selectedBodyPart?.let { bodyPart ->
                _addExerciseData.value.selectedCategory?.let { category ->
                    repo.addTemplateExercise(
                        Exercise(
                            _addExerciseData.value.name,
                            bodyPart = bodyPart,
                            category = category,
                            true
                        )
                    )
                    _addExerciseData.update { old -> old.copy(navigateBack = true) }
                }
            }
        }
    }

    /**
     * Updates the selected filter and ensures only the selected filter is marked as selected.
     *
     * @param filter The `FilterItem` to mark as selected.
     */
    fun onFilterChange(filter: FilterItem) {
        when (filter.filter) {
            is Filter.CategoryFilter -> {
                _addDialogExerciseData.update {
                    it.copy(
                        categoryFilters = updateSelection(filter, it.categoryFilters),
                        toShow = updateSelection(filter, it.categoryFilters)
                    )
                }
                _addExerciseData.update { it.copy(selectedCategory = filter.filter.category) }
            }

            is Filter.BodyPartFilter -> {
                _addDialogExerciseData.update {
                    it.copy(
                        bodyPartFilters = updateSelection(filter, it.bodyPartFilters),
                        toShow = updateSelection(filter, it.bodyPartFilters)
                    )
                }
                _addExerciseData.update { it.copy(selectedBodyPart = filter.filter.bodyPart) }
            }
        }
    }

    /**
     * Updates the selection state for a list of filters based on the given filter.
     *
     * @param selectedFilter The filter to mark as selected.
     * @param filters The list of filters to update.
     * @return A new list of filters with updated selection states.
     */
    private fun updateSelection(
        selectedFilter: FilterItem,
        filters: List<FilterItem>
    ): List<FilterItem> {
        return filters.map { item ->
            item.copy(selected = (item.name == selectedFilter.name))
        }
    }
}