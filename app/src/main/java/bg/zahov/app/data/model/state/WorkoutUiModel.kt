package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.WorkoutManagerViewModel
import bg.zahov.app.data.model.WorkoutState

data class WorkoutManagerUiModel(
    val trailingWorkoutVisibility: Int = View.GONE,
    val openWorkout: Boolean = false
)

object WorkoutManagerUiMapper {
    fun map(state: WorkoutManagerViewModel.State) = when (state) {
        is WorkoutManagerViewModel.State.Active -> WorkoutManagerUiModel(
            trailingWorkoutVisibility = state.visibility,
            openWorkout = state.openWorkout
        )

        is WorkoutManagerViewModel.State.Inactive -> WorkoutManagerUiModel(trailingWorkoutVisibility = state.visibility)
        is WorkoutManagerViewModel.State.Minimized -> WorkoutManagerUiModel(
            trailingWorkoutVisibility = state.visibility
        )
    }
}