package bg.zahov.app.data.model

import bg.zahov.app.ui.workout.WorkoutViewModel

data class OnGoingWorkoutUiModel(
    val isRestActive: Boolean = false,
    val rest: String = ""
)

object OnGoingWorkoutUiMapper {
    fun map(state: WorkoutViewModel.State) = when (state) {
        is WorkoutViewModel.State.Default -> OnGoingWorkoutUiModel()
        is WorkoutViewModel.State.Rest -> OnGoingWorkoutUiModel(true, state.time)
    }
}
