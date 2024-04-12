package bg.zahov.app.data.model.state

import bg.zahov.app.ui.signup.SignupViewModel

data class SignupUiModel(
    val authenticated: Boolean = false,
    val errorMessage: String? = null,
    val notifyMessage: String? = null
)

object SignupUiMapper {
    fun map(state: SignupViewModel.State): SignupUiModel = when (state) {
        is SignupViewModel.State.Authentication -> SignupUiModel(authenticated = true)
        is SignupViewModel.State.Error -> SignupUiModel(errorMessage = state.eMessage)
        is SignupViewModel.State.Notify -> SignupUiModel(notifyMessage = state.nMessage)
    }
}
