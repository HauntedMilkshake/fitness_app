package bg.zahov.app.ui.authentication.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getUserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignupViewModel(application: Application) : AndroidViewModel(application) {
    private val auth by lazy {
        application.getUserProvider()
    }
    private val _uiState = MutableStateFlow(SignupUiState())
    val state = _uiState.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _uiState.update { old ->
            old.copy(username = newUsername)
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { old ->
            old.copy(email = newEmail)
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { old ->
            old.copy(password = newPassword)
        }
    }

    fun onConfirmPasswordChange(newPassword: String) {
        _uiState.update { old ->
            old.copy(confirmPassword = newPassword)
        }
    }

    fun onPasswordVisibilityChange(visibility: Boolean) {
        _uiState.update { old ->
            old.copy(passwordVisibility = !visibility)
        }
    }

    fun signUp() {

        if (areFieldsEmpty(
                _uiState.value.username,
                _uiState.value.email,
                _uiState.value.password,
                _uiState.value.confirmPassword
            )
        ) {
            showMessage("Do not leave empty fields")
            return
        }

        if (!(_uiState.value.email.isEmail())) {
            showMessage("Email is not valid")
            return
        }

        if (_uiState.value.password != _uiState.value.confirmPassword || _uiState.value.password.length < 6) {
            showMessage("Make sure the passwords are matching and at least 6 characters long")
            return
        }

        viewModelScope.launch {
            try {
                auth.signup(_uiState.value.email, _uiState.value.password)
                    .addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            it.user?.uid?.let {
                                auth.createDataSources(_uiState.value.username, it)
                                _uiState.update { old ->
                                    old.copy(isUserAuthenticated = true)
                                }
                            } ?: run {
                                showMessage("There was an error while attempting to log in")
                            }
                        }
                    }
                    .addOnFailureListener {
                        showMessage("There was an error while attempting to log in")
                    }
            } catch (e: CancellationException) {
                showMessage(e.message ?: "There is a problem with the services")
            }

        }

    }

    private fun areFieldsEmpty(
        userName: String?,
        email: String?,
        pass: String?,
        confirmPass: String?
    ) = listOf(userName, email, pass, confirmPass).any { it.isNullOrEmpty() }

    private fun showMessage(text: String? = null) {
        _uiState.update { old ->
            old.copy(notifyUser = text)
        }
    }

    fun messageShown() {
        showMessage(null)
    }
}

data class SignupUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisibility: Boolean = false,
    val isUserAuthenticated: Boolean = false,
    val notifyUser: String? = null
)