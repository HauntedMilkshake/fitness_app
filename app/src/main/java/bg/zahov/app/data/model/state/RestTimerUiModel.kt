package bg.zahov.app.data.model.state

import bg.zahov.app.ui.workout.rest.RestTimerViewModel

data class RestTimerUiModel(
    val isCustomTimerVisible: Boolean = false,
    val isCountdown: Boolean = false,
    val isAddingCustomTimer: Boolean = false,
    var time: String = "",
    val finished: Boolean = false
)

object RestTimerUiModelMapper {
    fun map(state: RestTimerViewModel.State) = when (state) {
        RestTimerViewModel.State.Default -> RestTimerUiModel()
        RestTimerViewModel.State.AddingCustomTimer -> RestTimerUiModel(isAddingCustomTimer = true)
        is RestTimerViewModel.State.CountDown -> RestTimerUiModel(
            isCustomTimerVisible = false,
            isCountdown = true,
            time = state.timer
        )

        RestTimerViewModel.State.OnTimerFinished -> RestTimerUiModel(finished = true)
    }
}
