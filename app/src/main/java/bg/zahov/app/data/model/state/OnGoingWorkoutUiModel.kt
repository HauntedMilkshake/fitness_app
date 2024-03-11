package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.ui.workout.WorkoutViewModel

data class OnGoingWorkoutUiModel(
    val restTimerVisibility: Int = View.GONE,
    val rest: String = "",
    val message: String? = null
)

object OnGoingWorkoutUiMapper {
    fun map(state: WorkoutViewModel.State) = when (state) {
        is WorkoutViewModel.State.Default -> OnGoingWorkoutUiModel()
        is WorkoutViewModel.State.Rest -> OnGoingWorkoutUiModel(View.VISIBLE, state.time)
        is WorkoutViewModel.State.Error -> OnGoingWorkoutUiModel(message = state.message)
    }
}
