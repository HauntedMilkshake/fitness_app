package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.data.model.Workout
import bg.zahov.app.ui.history.HistoryViewModel

data class HistoryUiModel(
    val workouts: List<Workout> = listOf(),
    val loadingVisibility: Int = View.GONE,
    val workoutVisibility: Int = View.VISIBLE,
)
object HistoryUiMapper {
    fun map(state: HistoryViewModel.State) = when(state) {
        is HistoryViewModel.State.Data -> HistoryUiModel( workouts = state.workouts)
        HistoryViewModel.State.Default -> HistoryUiModel()
        is HistoryViewModel.State.Loading -> HistoryUiModel(loadingVisibility = View.VISIBLE, workoutVisibility = View.GONE)
    }
}