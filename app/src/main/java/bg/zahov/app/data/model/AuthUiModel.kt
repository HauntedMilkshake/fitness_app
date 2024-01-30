package bg.zahov.app.data.model

import bg.zahov.app.AuthViewModel

data class AuthUiModel(
    val isAuthenticated: Boolean = false
)

object AuthUiModelMapper {
    fun map(state : AuthViewModel.State) = when(state) {
        is AuthViewModel.State.Authenticated -> AuthUiModel(state.isAuthenticated)
        AuthViewModel.State.Default -> AuthUiModel()
    }
}
