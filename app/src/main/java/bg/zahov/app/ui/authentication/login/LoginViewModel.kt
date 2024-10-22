package bg.zahov.app.ui.authentication.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getUserProvider
import bg.zahov.app.ui.authentication.AuthenticationState
import bg.zahov.app.ui.authentication.UiInfo
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.IllegalArgumentException


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val userMessage: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val auth by lazy {
        application.getUserProvider()
    }

    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }

    fun onEmailChanged(email: String) {
        _uiState.update { old ->
            old.copy(email = email)
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { old ->
            old.copy(password = password)
        }
    }

    fun onPasswordVisibilityChanged() {
        _uiState.update { old ->
            old.copy(isPasswordVisible = old.isPasswordVisible.not())
        }
    }

    fun sendPasswordResetEmail() {
        val currentEmail = _uiState.value.email

        if (currentEmail.isEmpty()) {
            showMessage("Email must not be empty")
            return
        }

        if (!currentEmail.isEmail()) {
            showMessage("Email is not valid")
            return
        }

        viewModelScope.launch {
            auth.passwordResetByEmail(currentEmail)
                .addOnSuccessListener { showMessage("Rest password email has been sent") }
                .addOnFailureListener { showMessage("Couldn't send reset password email!") }
        }
    }

    fun messageShown() {
        showMessage(null)
    }

    fun login() {
        val currentEmail = _uiState.value.email
        val currentPassword = _uiState.value.password

        if (currentEmail.isEmpty() || currentPassword.isEmpty()) {
            showMessage("Don't leave empty fields")
            return
        }

        if (!currentEmail.isEmail()) {
            showMessage("Email not valid")
            return
        }

        viewModelScope.launch {
            try {
                auth.login(currentEmail, currentPassword)
                    .addOnSuccessListener {
                        _uiState.update { old ->
                            old.copy(isUserLoggedIn = true)}
                    }
                    .addOnFailureListener {
                        it.message?.let { it1 -> showMessage(it1) }
                    }
            } catch (e: IllegalArgumentException) {
                serviceError.initiateCountdown()
            }
        }
    }

    private fun showMessage(text: String? = null) {
        _uiState.update { old ->
            old.copy(userMessage = text)
        }
    }
}