package bg.zahov.app.data.model

import bg.zahov.app.ui.signup.SignupViewModel

data class SignupUiModel(
    val authenticated: Boolean = false,
    val errorMessage: String? = null
)

object SignupUiMapper {
    fun map(state: SignupViewModel.State): SignupUiModel = when (state) {
        is SignupViewModel.State.Default -> SignupUiModel()
        is SignupViewModel.State.Authenticated -> SignupUiModel(authenticated = true)
        is SignupViewModel.State.FailedAuthentication -> SignupUiModel()
    }
}
