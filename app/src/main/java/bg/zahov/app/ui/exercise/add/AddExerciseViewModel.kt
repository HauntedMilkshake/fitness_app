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
import bg.zahov.fitness.app.R
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

    private val _newExerciseData = MutableStateFlow(NewExerciseData())
    val newExerciseData: StateFlow<NewExerciseData> = _newExerciseData

    /**
     * Data class representing the UI state of the Add Exercise screen.
     *
     * @property name The name of the exercise.
     * @property bodyPartFilters List of filters for selecting a body part.
     * @property categoryFilters List of filters for selecting a category.
     * @property uiEventState The current state of the UI, such as showing or hiding dialogs.
     * @property userMessageId A resource ID for user-facing messages (e.g., error or success).
     */
    data class NewExerciseData(
        val name: String = "",
        val bodyPartFilters: List<FilterItem> = enumValues<BodyPart>().flatMap { bodyPart ->
            listOf(FilterItem(filter = Filter.BodyPartFilter(bodyPart)))
        },
        val categoryFilters: List<FilterItem> = enumValues<Category>().flatMap { category ->
            listOf(FilterItem(filter = Filter.CategoryFilter(category)))
        },
        val uiEventState: EventState = EventState.HideDialog,
        val userMessageId: Int? = null,
    )

    /**
     * Enum representing the possible UI states or events for the Add Exercise screen.
     */
    enum class EventState {
        HideDialog,          // Dialog is hidden
        ShowBodyPartFilter,  // Dialog for filtering by body part is shown
        ShowCategoryFilter,  // Dialog for filtering by category is shown
        NavigateBack,        // Triggers navigation back
    }

    /**
     * Updates the exercise name in the UI state.
     *
     * @param name The new exercise name to set.
     */
    fun onNameChange(name: String) {
        _newExerciseData.update { old ->
            old.copy(name = name)
        }
    }

    /**
     * Updates the current UI event state.
     *
     * @param event The new event state to transition to.
     */
    fun changeEvent(event: EventState) {
        _newExerciseData.update { old -> old.copy(uiEventState = event) }
    }

    /**
     * Attempts to add a new exercise. Validates input and updates the UI state
     * with success or error messages.
     */
    fun addExercise() {
        viewModelScope.launch {
            getSelectedBodyPart()?.let { bodyPart ->
                getSelectedCategory()?.let { category ->
                    repo.addTemplateExercise(
                        Exercise(
                            _newExerciseData.value.name,
                            bodyPart = bodyPart,
                            category = category,
                            true
                        )
                    )
                    changeEvent(EventState.NavigateBack)
                }
            }
        }
    }

    fun checkButtonAvailability() =
        (getSelectedBodyPart() == null || getSelectedCategory() == null || _newExerciseData.value.name.isBlank()).not()

    /**
     * Retrieves the selected body part filter from the list of filters.
     *
     * @return The selected `BodyPart`, or `null` if none is selected.
     */
    fun getSelectedBodyPart(): BodyPart? {
        return (_newExerciseData.value.bodyPartFilters
            .firstOrNull { it.selected && it.filter is Filter.BodyPartFilter }
            ?.filter as? Filter.BodyPartFilter)?.bodyPart
    }

    /**
     * Retrieves the selected category filter from the list of filters.
     *
     * @return The selected `Category`, or `null` if none is selected.
     */
    fun getSelectedCategory(): Category? {
        return (_newExerciseData.value.categoryFilters
            .firstOrNull { it.selected && it.filter is Filter.CategoryFilter }
            ?.filter as? Filter.CategoryFilter)?.category
    }

    /**
     * Updates the selected category filter in the UI state.
     *
     * @param filter The `FilterItem` to mark as selected.
     */
    fun onCategoryFilterChange(filter: FilterItem) {
        _newExerciseData.update { old ->
            old.copy(
                categoryFilters = old.categoryFilters.map { item ->
                    item.copy(selected = (item.name == filter.name))
                }
            )
        }
    }

    /**
     * Updates the selected body part filter in the UI state.
     *
     * @param filter The `FilterItem` to mark as selected.
     */
    fun onBodyPartFilterChange(filter: FilterItem) {
        _newExerciseData.update { old ->
            old.copy(
                bodyPartFilters = old.bodyPartFilters.map { item ->
                    item.copy(selected = (item.name == filter.name))
                }
            )
        }
    }
}