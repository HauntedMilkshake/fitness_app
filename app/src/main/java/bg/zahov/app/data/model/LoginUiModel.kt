package bg.zahov.app.data.model

import bg.zahov.app.ui.login.LoginViewModel

data class LoginUiModel(
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val passwordErrorMessage: String? = null,
    val passwordLinkMessage: String? = null,
)

object LoginUiMapper {
    fun map(state: LoginViewModel.State) = when (state) {
        is LoginViewModel.State.Default -> LoginUiModel()
        is LoginViewModel.State.Authenticated -> LoginUiModel(true)
        is LoginViewModel.State.AuthError -> LoginUiModel(errorMessage = state.errorMessage)
        is LoginViewModel.State.PasswordLinkError -> LoginUiModel(passwordErrorMessage = state.errorMessage)
        is LoginViewModel.State.PasswordLink -> LoginUiModel(passwordLinkMessage = state.message)
    }
}