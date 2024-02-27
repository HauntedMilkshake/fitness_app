package bg.zahov.app.data.model.state

import bg.zahov.app.ui.home.HomeViewModel

data class HomeUiModel(
    val isLoading: Boolean = false
)

object HomeUiMapper {
    fun map(state: HomeViewModel.State) = when(state) {
        HomeViewModel.State.Default -> HomeUiModel()
        is HomeViewModel.State.Loading -> HomeUiModel(true)
    }
}