package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryInfo
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryViewModel

data class ExerciseHistoryUiModel(
    val data: List<ExerciseHistoryInfo> = listOf(),
    val loadingVisibility: Int = View.GONE,
    val recyclerViewVisibility: Int = View.VISIBLE,
    val shutdown: Boolean = false,
    val message: String? = null,
)

object ExerciseHistoryUiMapper {
    fun map(state: ExerciseHistoryViewModel.State) = when (state) {
        is ExerciseHistoryViewModel.State.Data -> ExerciseHistoryUiModel(data = state.data)
        ExerciseHistoryViewModel.State.Default -> ExerciseHistoryUiModel()
        is ExerciseHistoryViewModel.State.Error -> ExerciseHistoryUiModel(shutdown = state.shutdown)
        is ExerciseHistoryViewModel.State.Loading -> ExerciseHistoryUiModel(loadingVisibility = View.VISIBLE, recyclerViewVisibility = View.GONE)
        is ExerciseHistoryViewModel.State.Notify -> ExerciseHistoryUiModel(message = state.message)
    }
}