package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.FilterWrapper

/**
 * UI state representation for the exercise view, including a list of exercises,
 * filters, loading state, search query, current action flag, and dialog visibility.
 *
 * @property exercises The list of exercises available in the UI.
 * @property filters The current applied filters.
 * @property loading Indicates whether data is currently loading.
 * @property search The current search query.
 * @property flag The current action state for exercises (e.g., Adding, Replacing).
 * @property showDialog Indicates whether to show a dialog.
 */
data class ExerciseScreenData(
    val exercises: List<ExerciseData> = listOf(),
    val filters: List<FilterWrapper> = listOf(),
    val loading: Boolean = true,
    val search: String = "",
    val flag: ExerciseFlag = ExerciseFlag.Default,
    val showDialog: Boolean = false,
    val navigateBack: Boolean = false
)

/**
 * Wrapper class for exercise data, including its name, associated body part, category,
 * and selection state.
 *
 * @property name The name of the exercise.
 * @property bodyPart The body part associated with the exercise.
 * @property category The category of the exercise.
 * @property selected Indicates whether the exercise is selected.
 * @property toShow Indicates whether the exercise should be showed on screen.
 */
data class ExerciseData(
    val name: String,
    val bodyPart: BodyPart,
    val category: Category,
    val selected: Boolean = false,
    val toShow: Boolean = true,
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