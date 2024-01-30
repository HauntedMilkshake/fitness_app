package bg.zahov.app.data.model

import bg.zahov.app.ui.login.LoginViewModel
import okhttp3.internal.notify

data class LoginUiModel(
    val isAuthenticated: Boolean = false,
    val notifyMessage: String? = null,
)

object LoginUiMapper {
    fun map(state: LoginViewModel.State) = when (state) {
        is LoginViewModel.State.Default -> LoginUiModel()
        is LoginViewModel.State.Authenticated -> LoginUiModel(true)
        is LoginViewModel.State.Notify -> LoginUiModel(notifyMessage = state.message)
    }
}