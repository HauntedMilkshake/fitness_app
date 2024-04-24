package bg.zahov.app.data.model.state

import bg.zahov.app.ui.error.ShuttingDownViewModel

data class ShutdownFragmentUiModel(
    val timer: String = "",
)
object ShutdownFragmentUiMapper {
    fun map(state: ShuttingDownViewModel.State) = when(state) {
        is ShuttingDownViewModel.State.CountDown -> ShutdownFragmentUiModel(timer = state.currentTime)
    }
}