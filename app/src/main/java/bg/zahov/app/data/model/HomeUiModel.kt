package bg.zahov.app.data.model

import bg.zahov.app.ui.home.HomeViewModel

data class HomeUiModel(
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
object HomeUiMapper {
    fun map(state: HomeViewModel.State) = when(state) {
        HomeViewModel.State.Default -> HomeUiModel()
        is HomeViewModel.State.Error -> HomeUiModel(errorMessage = state.message)
        is HomeViewModel.State.Loading -> HomeUiModel(isLoading = state.isLoading)
    }
}
