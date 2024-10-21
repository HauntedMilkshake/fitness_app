package bg.zahov.app.ui.authentication.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getUserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.IllegalArgumentException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<UiState>(UiState.Default)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var counter:Int = 1

    private val auth by lazy {
        application.getUserProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }

    fun login(email: String, password: String) {
        counter++
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = UiState.Error(message = "Don't leave empty fields", counter = counter)
            return
        }

        if (!email.isEmail()) {
            _uiState.value = UiState.Error(message = "Email not valid", counter = counter)
            return
        }

        viewModelScope.launch {
            try {
                auth.login(email, password)
                    .addOnSuccessListener {
                        _uiState.value = UiState.Authenticated(isAuthenticated = true)
                    }
                    .addOnFailureListener {
                        _uiState.value =
                            it.message?.let {
                                it1 ->
                                UiState.Error(message = it1, counter = counter)
                            }!!
                    }
            } catch (e: IllegalArgumentException) {
                serviceError.initiateCountdown()
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        counter++
        if (email.isEmpty()) {
            _uiState.value = UiState.Error(message = "Email must not be empty", counter = counter)
            return
        }

        if (!email.isEmail()) {
            _uiState.value = UiState.Error(message = "Email is not valid", counter = counter)
            return
        }

        viewModelScope.launch {
            auth.passwordResetByEmail(email)
                .addOnSuccessListener {
                    _uiState.value = UiState.Notification(
                        message = "Rest password email has been sent",
                        counter = counter
                    )
                }
                .addOnFailureListener {
                    _uiState.value = UiState.Error(
                        message = "Couldn't send reset password email!",
                        counter = counter
                    )
                }
        }
    }
    sealed interface UiState {
        object Default : UiState
        data class Authenticated(var isAuthenticated: Boolean) : UiState
        data class Error(var message: String, var counter: Int) : UiState
        data class Notification(var message: String, var counter: Int) : UiState
    }
}

