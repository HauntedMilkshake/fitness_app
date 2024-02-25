package bg.zahov.app.data.model.state

import bg.zahov.app.ui.loading.LoadingViewModel

data class LoadingUiModel(
    val isLoading: Boolean = true,
    val message: String? = null,
)

object LoadingUiMapper {
    fun map(state: LoadingViewModel.State) = when (state) {
        is LoadingViewModel.State.Error -> LoadingUiModel(message = state.message)
        is LoadingViewModel.State.Loading -> LoadingUiModel(isLoading = state.isDataLoading)
    }
}