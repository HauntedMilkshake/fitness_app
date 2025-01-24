package bg.zahov.app.ui.authentication.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.UserProvider
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the signup screen
 *
 * @param auth gives access to firebase authentication
 */
class SignupViewModel(private val auth: UserProvider = Inject.userProvider) : ViewModel() {
    private val _uiState = MutableStateFlow(SignupUiState())

    val uiState: StateFlow<SignupUiState> = _uiState


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

    /**
     * Initiates the signup process with the provided email and password.
     */
    fun signUp() {
        viewModelScope.launch {
            try {
                auth.signup(_uiState.value.email, _uiState.value.password).user?.uid?.let {
                    auth.createDataSources(_uiState.value.username, it)
                } ?: showMessage("There was an error while attempting to log in")
            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        showMessage(
                            e.message ?: "There is already another user with the same email"
                        )
                    }

                    is CancellationException -> throw e
                    else -> showMessage("Something went wrong")

                }
            }

        }
    }

    /**
     * Updates the notification message in the UI state.
     *
     * @param text The message to be shown to the user.
     */
    private fun showMessage(text: String? = null) {
        _uiState.update { old ->
            old.copy(notifyUser = text)
        }
    }

    /**
     * Resets the notification message to null after it has been shown.
     */
    fun messageShown() {
        showMessage(null)
    }
}

/**
 * Data class representing the UI state for the signup screen.
 *
 * @property username The username input by the user.
 * @property email The email input by the user.
 * @property password The password input by the user.
 * @property confirmPassword The confirmed password input by the user.
 * @property passwordVisibility Indicates whether the password is visible.
 * @property isUserAuthenticated Indicates whether the user is authenticated.
 * @property notifyUser A message to notify the user, or null if no message.
 */
data class SignupUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisibility: Boolean = false,
    val notifyUser: String? = null
)