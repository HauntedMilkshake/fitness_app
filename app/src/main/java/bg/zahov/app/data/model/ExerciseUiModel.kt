package bg.zahov.app.data.model

import bg.zahov.app.ui.exercise.ExerciseViewModel

data class ExerciseUiModel(
    val isLoading: Boolean = false,
    val error: String? = null,
    val areThereResults: Boolean = true,
)

object ExerciseUiMapper {
    fun map(state: ExerciseViewModel.State) = when (state) {
        ExerciseViewModel.State.Default -> ExerciseUiModel()
        is ExerciseViewModel.State.Loading -> ExerciseUiModel(isLoading = true)
        is ExerciseViewModel.State.ErrorFetching -> ExerciseUiModel(
            error = state.error,
            areThereResults = false
        )

        is ExerciseViewModel.State.NoResults -> ExerciseUiModel(areThereResults = false)
    }
}
