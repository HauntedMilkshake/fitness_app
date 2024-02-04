package bg.zahov.app.data.model

import bg.zahov.app.ui.signup.SignupViewModel

data class SignupUiModel(
    val authenticated: Boolean = false,
    val errorMessage: String? = null,
    val notifyMessage: String? = null,
    val shutdown: Boolean = false
)

object SignupUiMapper {
    fun map(state: SignupViewModel.State): SignupUiModel = when (state) {
        is SignupViewModel.State.Authentication -> SignupUiModel(authenticated = true)
        is SignupViewModel.State.Error -> SignupUiModel(errorMessage = state.eMessage, shutdown = state.shutdown)
        is SignupViewModel.State.Notify -> SignupUiModel(notifyMessage = state.nMessage)
    }
}
