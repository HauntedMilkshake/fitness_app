package bg.zahov.app.ui.authentication

sealed interface AuthenticationState {
    data object Authenticate : AuthenticationState
    data class Default(val uiInfo: UiInfo) : AuthenticationState
    data class Notify(val uiInfo: UiInfo, val message: String, val stateCounter: Int = 0) :
        AuthenticationState
}

data class UiInfo(
    var username: String = "",
    var mail: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var passwordVisibility: Boolean = false
)