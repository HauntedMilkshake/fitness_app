package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.ui.exercise.ExerciseViewModel

data class ExerciseUiModel(
    val loadingVisibility: Int = View.GONE,
    val error: String? = null,
    val noResultsVisibility: Int = View.GONE,
)

object ExerciseUiMapper {
    fun map(state: ExerciseViewModel.State) = when (state) {
        ExerciseViewModel.State.Default -> ExerciseUiModel()
        is ExerciseViewModel.State.Loading -> ExerciseUiModel(loadingVisibility = state.loadingVisibility)
        is ExerciseViewModel.State.ErrorFetching -> ExerciseUiModel(
            error = state.error,
            noResultsVisibility = View.VISIBLE
        )

        is ExerciseViewModel.State.NoResults -> ExerciseUiModel(noResultsVisibility = View.VISIBLE)
    }
}
