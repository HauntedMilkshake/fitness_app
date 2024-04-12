package bg.zahov.app.data.model.state

import bg.zahov.app.ServiceErrorStateViewModel

data class ServiceStateUiModel(
    val action: Int? = null,
    val shutdown: Boolean = false
)
object ServiceStateUiMapper {
    fun map(state: ServiceErrorStateViewModel.State) = when(state) {
        is ServiceErrorStateViewModel.State.NavigateToTimer -> ServiceStateUiModel(action = state.action)
        is ServiceErrorStateViewModel.State.Shutdown -> ServiceStateUiModel(shutdown = state.shouldShutdown)
    }
}