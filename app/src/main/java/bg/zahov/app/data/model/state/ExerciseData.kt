package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.FilterWrapper
import bg.zahov.app.ui.exercise.ExerciseFlag
import bg.zahov.app.ui.exercise.ExercisesWrapper

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
data class ExerciseData(
    val exercises: List<ExercisesWrapper> = listOf(),
    val filters: List<FilterWrapper> = listOf(),
    val loading: Boolean = true,
    val search: String = "",
    val flag: ExerciseFlag = ExerciseFlag.Default,
    val showDialog: Boolean = false
)