package bg.zahov.app.data.model

import bg.zahov.app.ui.home.HomeViewModel

data class HomeUiModel(
    val isLoading: Boolean = true,
    val username: String = "",
    val number: Int = 0,
    val errorMessage: String? = null
)
object HomeUiMapper {
    fun map(state: HomeViewModel.State) = when(state) {
        HomeViewModel.State.Default -> HomeUiModel()
        is HomeViewModel.State.Error -> HomeUiModel()
        is HomeViewModel.State.Loading -> HomeUiModel(isLoading = state.isLoading)
        is HomeViewModel.State.UserWorkouts -> HomeUiModel()
        is HomeViewModel.State.Username -> HomeUiModel(false, state.username, 0)
        is HomeViewModel.State.Workouts -> HomeUiModel()
    }
}
