package bg.zahov.app.ui.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Login screen that handles user input, validation, and interaction with the
 * authentication backend service. It is responsible for managing the UI state and updating the
 * UI based on user interactions such as entering email, password, or toggling password visibility.
 *
 * @property uiState A [StateFlow] of [LoginData], which contains the current UI state including email, password, message, password visibility and login check.
 *
 * @constructor
 * @param auth Injected user authentication provider that handles login and password reset logic.
 * @param serviceError Injected error handler that manages errors and starts the error-handling countdown.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: UserProvider,
    private val serviceError: ServiceErrorHandler,
) : ViewModel() {
    /**
     * Data class that represents the UI state for the login screen.
     *
     * @property email The email input from the user.
     * @property password The password input from the user.
     * @property message An optional message to be displayed (e.g., errors or success messages).
     * @property passwordVisibility Boolean flag indicating whether the password is visible or hidden.
     */
    data class LoginData(
        var email: String = "",
        var password: String = "",
        val message: String? = null,
        var passwordVisibility: Boolean = false,
    )

    // Holds the current UI state as a MutableStateFlow to observe and react to changes.
    private val _uiState = MutableStateFlow(LoginData())
    val uiState: StateFlow<LoginData> = _uiState

    /**
     * Updates the password in the UI state when the user changes the password input.
     *
     * @param password The new password entered by the user.
     */
    fun onPasswordChange(password: String) {
        _uiState.update { old -> old.copy(password = password) }
    }

    /**
     * Updates the email in the UI state when the user changes the email input.
     *
     * @param email The new email entered by the user.
     */
    fun onEmailChange(email: String) {
        _uiState.update { old -> old.copy(email = email) }
    }

    /**
     * Toggles the password visibility in the UI state. This is triggered when the user clicks on
     * the "show/hide password" button.
     */
    fun onPasswordVisibilityChange() {
        _uiState.update { old -> old.copy(passwordVisibility = !old.passwordVisibility) }
    }

    /**
     * Attempts to log in the user using the current email and password from the UI state.
     * Validates the email format and checks for empty fields. Upon success, updates the logged-in state.
     * If an error occurs, displays the error message.
     */
    fun login() {
        viewModelScope.launch {
            try {
                auth.login(_uiState.value.email, _uiState.value.password)
            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthInvalidUserException -> {
                        _uiState.update { old -> old.copy(message = e.message) }
                    }

                    is FirebaseAuthInvalidCredentialsException -> {
                        _uiState.update { old -> old.copy(message = e.message) }
                    }
                }
            }
        }
    }

    /**
     * Sends a password reset email to the user's email.
     * Displays a success message upon successful email dispatch, or an error message if it fails.
     */
    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            auth.passwordResetByEmail(email = _uiState.value.email)
                .addOnSuccessListener { notifyChange("Rest password email has been sent") }
                .addOnFailureListener { notifyChange("Couldn't send reset password email!") }
        }
    }

    /**
     * Updates the message in the UI state to notify the user. This could be used to display errors,
     * success messages, or other important information.
     *
     * @param text The message to display, or `null` to clear the message.
     */
    private fun notifyChange(text: String? = null) {
        _uiState.update { old -> old.copy(message = text) }
    }

    /**
     * Clears the currently displayed message by calling [notifyChange] with `null`.
     * This is typically called after the user has dismissed or seen the message.
     */
    fun shownMessage() {
        notifyChange(null)
    }
}
