package bg.zahov.app.data.model

import bg.zahov.app.ui.workout.start.WorkoutViewModel

data class WorkoutUiModel(
    val errorMessage: String? = null,
    val shutdown: Boolean = false
)

object WorkoutUiMapper {
    fun map(state: WorkoutViewModel.State) = when(state) {
        is WorkoutViewModel.State.Error -> WorkoutUiModel(state.error, state.shutdown)
    }
}
