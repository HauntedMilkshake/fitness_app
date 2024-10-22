package bg.zahov.app.ui.authentication.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getUserProvider
import bg.zahov.app.ui.authentication.AuthenticationState
import bg.zahov.app.ui.authentication.UiInfo
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(application: Application) : AndroidViewModel(application) {
    private val auth by lazy {
        application.getUserProvider()
    }
    private var stateCounter = 0
    private val uiInfo = UiInfo()
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState.Default(uiInfo))
    val state = _state.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        uiInfo.username = newUsername
        updateState(uiInfo)
    }

    fun onEmailChange(newEmail: String) {
        uiInfo.mail = newEmail
        updateState(uiInfo)

    }

    fun onPasswordChange(newPassword: String) {
        uiInfo.password = newPassword
        updateState(uiInfo)

    }

    fun onConfirmPasswordChange(newPassword: String) {
        uiInfo.confirmPassword = newPassword
        updateState(uiInfo)

    }

    fun onPasswordVisibilityChange(visibility: Boolean) {
        uiInfo.passwordVisibility = !visibility
        updateState(uiInfo)

    }

    private fun updateState(newUiInfo: UiInfo) {
        stateCounter++
        _state.value = when (val currentState = _state.value) {
            AuthenticationState.Authenticate -> AuthenticationState.Authenticate
            is AuthenticationState.Default -> AuthenticationState.Default(newUiInfo, stateCounter)
            is AuthenticationState.Notify -> AuthenticationState.Notify(
                newUiInfo,
                currentState.message,
                stateCounter
            )
        }
    }

    fun signUp(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ) {
        stateCounter += 1

        if (areFieldsEmpty(userName, email, password)) {
            _state.value =
                AuthenticationState.Notify(uiInfo, "Do not leave empty fields", stateCounter)
            return
        }

        if (!email.isEmail()) {
            _state.value = AuthenticationState.Notify(uiInfo, "Email is not valid", stateCounter)
            return
        }

        if (password != confirmPassword || password.length < 6) {
            _state.value =
                AuthenticationState.Notify(
                    uiInfo,
                    "Make sure the passwords are matching and at least 6 characters long",
                    stateCounter
                )
            return
        }

        viewModelScope.launch {
            try {
                auth.signup(email, password)
                    .addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            it.user?.uid?.let {
                                auth.createDataSources(userName, it)
                                _state.value = AuthenticationState.Authenticate

                            } ?: run {
                                _state.value = AuthenticationState.Notify(
                                    uiInfo,
                                    "There was an error while attempting to log in", stateCounter
                                )
                            }
                        }
                    }
                    .addOnFailureListener {
                        _state.value = AuthenticationState.Notify(
                            uiInfo,
                            it.message ?: "Unexpected error",
                            stateCounter
                        )

                    }
            } catch (e: CancellationException) {
                _state.value =
                    AuthenticationState.Notify(
                        uiInfo,
                        e.message ?: "There is a problem with the services",
                        stateCounter
                    )
            }

        }

    }

    private fun areFieldsEmpty(userName: String?, email: String?, pass: String?) =
        listOf(userName, email, pass).any { it.isNullOrEmpty() }

}


