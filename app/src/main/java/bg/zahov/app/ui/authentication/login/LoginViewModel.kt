package bg.zahov.app.ui.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.lang.IllegalArgumentException

/**
 * ViewModel for the Login screen that handles user input, validation, and interaction with the
 * authentication backend service. It is responsible for managing the UI state and updating the
 * UI based on user interactions such as entering email, password, or toggling password visibility.
 *
 * @property uiState A [StateFlow] of [UiInfo], which contains the current UI state including email, password, message, password visibility and login check.
 *
 * @constructor
 * @param userProvider Injected user authentication provider that handles login and password reset logic.
 * @param errorProvider Injected error handler that manages errors and starts the error-handling countdown.
 */
class LoginViewModel(
    userProvider: UserProvider = Inject.userProvider,
    errorProvider: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    // The authentication service for handling login and password reset.
    private val auth by lazy {
        userProvider
    }

    private val serviceError by lazy {
        errorProvider
    }

    // Holds the current UI state as a MutableStateFlow to observe and react to changes.
    private val _uiState = MutableStateFlow(UiInfo())
    val uiState: StateFlow<UiInfo> = _uiState

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
        val currentEmail = _uiState.value.email
        val currentPassword = _uiState.value.password
        if (currentEmail.isEmpty() || currentPassword.isEmpty()) {
            notifyChange("Don't leave empty fields")
            return
        }

        if (!currentEmail.isEmail()) {
            notifyChange("Email not valid")
            return
        }

        viewModelScope.launch {
            try {
                auth.login(currentEmail, currentPassword)
                    .addOnSuccessListener {
                        _uiState.update { old -> old.copy(isLoggedInfo = true) }
                    }
                    .addOnFailureListener {
                        it.message?.let { it1 -> notifyChange(it1) }
                    }
            } catch (e: IllegalArgumentException) {
                serviceError.initiateCountdown()
            }
        }
    }

    /**
     * Sends a password reset email to the user's email if it's valid and not empty.
     * Displays a success message upon successful email dispatch, or an error message if it fails.
     */
    fun sendPasswordResetEmail() {
        val email = _uiState.value.email
        if (email.isEmpty()) {
            notifyChange("Email must not be empty")
            return
        }

        if (!email.isEmail()) {
            notifyChange("Email is not valid")
            return
        }

        viewModelScope.launch {
            auth.passwordResetByEmail(email)
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

    /**
     * Data class that represents the UI state for the login screen.
     *
     * @property email The email input from the user.
     * @property password The password input from the user.
     * @property message An optional message to be displayed (e.g., errors or success messages).
     * @property passwordVisibility Boolean flag indicating whether the password is visible or hidden.
     * @property isLoggedInfo Boolean flag indicating whether the user is logged in or not.
     */
    data class UiInfo(
        var email: String = "",
        var password: String = "",
        val message: String? = null,
        var passwordVisibility: Boolean = false,
        var isLoggedInfo: Boolean = false
    )
}
