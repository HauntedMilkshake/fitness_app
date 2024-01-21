package bg.zahov.app.data.model

import bg.zahov.app.ui.login.LoginViewModel

data class LoginUiModel (
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)
object LoginUiMapper {
    fun map(state: LoginViewModel.State) = when(state) {
        is LoginViewModel.State.Default -> LoginUiModel()
        is LoginViewModel.State.Authenticated -> LoginUiModel(true)
        is LoginViewModel.State.Error -> LoginUiModel(errorMessage = "Log in failed try again")
        is LoginViewModel.State.ForgotPasswordLinkSent -> LoginUiModel(errorMessage = "Failed to send password reset link")
    }
}