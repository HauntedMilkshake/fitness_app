package bg.zahov.app.data.model.state

import bg.zahov.app.ui.login.LoginViewModel

data class LoginUiModel(
    val isAuthenticated: Boolean = false,
    val notifyMessage: String? = null,
    val errorMessage: String? = null,

    )

object LoginUiMapper {
    fun map(state: LoginViewModel.State) = when (state) {
        is LoginViewModel.State.Authenticated -> LoginUiModel(true)
        is LoginViewModel.State.Notify -> LoginUiModel(notifyMessage = state.nMessage)
        is LoginViewModel.State.Error -> LoginUiModel(
            errorMessage = state.eMessage
        )
    }
}