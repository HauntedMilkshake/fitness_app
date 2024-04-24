package bg.zahov.app.data.model.state

import bg.zahov.app.ui.exercise.info.ExerciseNavigationViewModel

data class ExerciseNavigationUiModel(
    val exerciseName: String = "",
    val message: String? = null
)

object ExerciseNavigationUiMapper {
    fun map(state: ExerciseNavigationViewModel.State) = when(state) {
        is ExerciseNavigationViewModel.State.Data -> ExerciseNavigationUiModel(state.exerciseName)
        is ExerciseNavigationViewModel.State.Error -> ExerciseNavigationUiModel(state.message)
    }
}