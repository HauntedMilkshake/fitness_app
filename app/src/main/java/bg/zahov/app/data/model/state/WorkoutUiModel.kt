package bg.zahov.app.data.model.state

import bg.zahov.app.WorkoutManagerViewModel
import bg.zahov.app.data.model.WorkoutState

data class WorkoutManagerUiModel(
    val state: WorkoutState = WorkoutState.INACTIVE,
)

object WorkoutManagerUiMapper {
    fun map(state: WorkoutManagerViewModel.State) = when (state) {
        is WorkoutManagerViewModel.State.Active -> WorkoutManagerUiModel(state.state)
    }
}