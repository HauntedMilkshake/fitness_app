package bg.zahov.app.data.model.state

import bg.zahov.app.ui.workout.start.StartWorkoutViewModel

data class StartWorkoutUiModel(
    val errorMessage: String? = null,
    val shutdown: Boolean = false,
    val isWorkoutActive: Boolean = false,
    val message: String? = null
)

object StartWorkoutUiMapper {
    fun map(state: StartWorkoutViewModel.State) = when (state) {
        is StartWorkoutViewModel.State.Error -> StartWorkoutUiModel(state.error, state.shutdown)
        is StartWorkoutViewModel.State.Active -> StartWorkoutUiModel(isWorkoutActive = state.isWorkoutActive, message = state.message)
    }
}
