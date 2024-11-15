package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterItem

/**
 * Data class representing the UI state of the Add Exercise screen.
 *
 * @property name The name of the exercise.
 * @property bodyPartFilters List of filters for selecting a body part.
 * @property categoryFilters List of filters for selecting a category.
 * @property uiAddExerciseEventState The current state of the UI, such as showing or hiding dialogs.
 * @property userMessageId A resource ID for user-facing messages (e.g., error or success).
 */
data class AddExerciseData(
    val name: String = "",
    val bodyPartFilters: List<FilterItem> = enumValues<BodyPart>().flatMap { bodyPart ->
        listOf(FilterItem(filter = Filter.BodyPartFilter(bodyPart)))
    },
    val categoryFilters: List<FilterItem> = enumValues<Category>().flatMap { category ->
        listOf(FilterItem(filter = Filter.CategoryFilter(category)))
    },
    val uiAddExerciseEventState: AddExerciseEventState = AddExerciseEventState.HideDialog,
    val userMessageId: Int? = null,
)

/**
 * Enum representing the possible UI states or events for the Add Exercise screen.
 */
enum class AddExerciseEventState {
    HideDialog,          // Dialog is hidden
    ShowBodyPartFilter,  // Dialog for filtering by body part is shown
    ShowCategoryFilter,  // Dialog for filtering by category is shown
    NavigateBack,        // Triggers navigation back
}