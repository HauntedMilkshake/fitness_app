package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterItem

/**
 * Represents the state for adding an exercise.
 *
 * @property name The name of the exercise being added. Defaults to an empty string.
 * @property navigateBack A flag indicating whether the UI should navigate back after an action. Defaults to `false`.
 * @property userMessageId An optional message ID for user notifications or actions. Defaults to `null`.
 * @property selectedBodyPart Saves the currently selected BodyPart filter. Defaults to `null`.
 * @property selectedCategory Saves the currently selected Category filter. Defaults to `null`.
 */
data class AddExerciseData(
    val name: String = "",
    val navigateBack: Boolean = false,
    val userMessageId: Int? = null,
    val selectedBodyPart: BodyPart? = null,
    val selectedCategory: Category? = null
){

    /**
     * Checks whether the "Add Exercise" button should be enabled based on
     * whether a name, body part, and category have been selected.
     *
     * @property isButtonAvailable is `true` if all required fields are filled; otherwise `false`.
     */
    val isButtonAvailable: Boolean
        get() = selectedBodyPart != null && selectedCategory != null && name.isNotEmpty()
}

/**
 * Represents the UI state for managing the exercise addition dialog.
 *
 * @property toShow A list of `FilterItem` objects currently displayed in the dialog. Defaults to an empty list.
 * @property showDialog A flag indicating whether the dialog is currently visible. Defaults to `false`.
 * @property bodyPartFilters A list of filters for body parts, initialized with all available body parts.
 * Each body part is wrapped in a `FilterItem` for tracking selection.
 * @property categoryFilters A list of filters for exercise categories, initialized with all available categories.
 * Each category is wrapped in a `FilterItem` for tracking selection.
 */
data class AddDialogExerciseData(
    val toShow: List<FilterItem> = listOf(),
    val showDialog: Boolean = false,
    val bodyPartFilters: List<FilterItem> = enumValues<BodyPart>().flatMap { bodyPart ->
        listOf(FilterItem(filter = Filter.BodyPartFilter(bodyPart)))
    },
    val categoryFilters: List<FilterItem> = enumValues<Category>().flatMap { category ->
        listOf(FilterItem(filter = Filter.CategoryFilter(category)))
    },
)