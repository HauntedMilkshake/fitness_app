package bg.zahov.app.data.model

import bg.zahov.app.WorkoutManagerViewModel

data class WorkoutManagerUiModel(
    val state: WorkoutState = WorkoutState.INACTIVE,
)

object WorkoutManagerUiMapper {
    fun map(state: WorkoutManagerViewModel.State) = when (state) {
        is WorkoutManagerViewModel.State.Active -> WorkoutManagerUiModel(state.state)
    }
}