package bg.zahov.app.ui.settings.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user profile editing logic.
 *
 * @property repo The user provider for accessing and updating user information.
 * @property errorHandler Handles service errors, particularly critical data null exceptions.
 */
class EditProfileViewModel(
    private val repo: UserProvider = Inject.userProvider,
    private val errorHandler: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    /**
     * UI state for EditProfileViewModel, containing properties related to authentication,
     * user credentials, password visibility, and notifications.
     *
     * @property authenticated Whether the user is authenticated.
     * @property username The current username.
     * @property password The current password.
     * @property passwordVisibility Controls the visibility of the password.
     * @property notify Optional notification message.
     */
    data class UiState(
        val authenticated: Boolean = false,
        val username: String = "",
        val password: String = "",
        val passwordVisibility: Boolean = false,
        val notify: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    /**
     * Updates the username in the UI state.
     *
     * @param username The new username to be set.
     */
    fun onUsernameChange(username: String) {
        _state.update { it.copy(username = username) }
    }

    /**
     * Updates the password in the UI state.
     *
     * @param password The new password to be set.
     */
    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    /**
     * Toggles the visibility of the password field.
     */
    fun onPasswordVisibilityChange() {
        _state.update { it.copy(passwordVisibility = !_state.value.passwordVisibility) }
    }

    init {
        viewModelScope.launch {
            try {
                repo.getUser().collect {
                    _state.update { old -> old.copy(username = it.name) }
                }
            } catch (e: CriticalDataNullException) {
                errorHandler.initiateCountdown()
            }
        }
    }

    /**
     * Updates the username in the repository and sets a notification message upon success or failure.
     */
    fun updateUsername() {
        val newUsername = _state.value.username
        viewModelScope.launch {
            repo.changeUserName(newUsername)
                .addOnSuccessListener {
                    _state.update { old ->
                        old.copy(
                            username = newUsername,
                            notify = "Successfully updated username"
                        )
                    }
                }
                .addOnFailureListener { notifyChange("Failed to updated username") }
        }
    }

    /**
     * Unlocks fields by authenticating with the provided password.
     *
     * @param password The password used to re-authenticate.
     */
    fun unlockFields(password: String) {
        viewModelScope.launch {
            repo.reauthenticate(password)
                .addOnSuccessListener {
                    _state.update { old ->
                        old.copy(
                            authenticated = true,
                            notify = "Successfully re-authenticated!"
                        )
                    }
                }
                .addOnFailureListener { notifyChange("Failed to re-authenticate!") }
        }
    }

    /**
     * Sends a password reset link to the user's registered email address.
     */
    fun sendPasswordResetLink() {
        viewModelScope.launch {
            repo.passwordResetForLoggedUser()
                .addOnSuccessListener { notifyChange("Successfully updated password") }
                .addOnFailureListener { notifyChange("Failed to send password reset link") }
        }
    }

    /**
     * Updates the user's password with the new password stored in the UI state.
     */
    fun updatePassword() {
        val newPassword = _state.value.password
        viewModelScope.launch {
            repo.updatePassword(newPassword)
                .addOnSuccessListener { notifyChange("Successfully updated password") }
                .addOnFailureListener { notifyChange("Failed to update password") }
        }
    }

    /**
     * Clears the notification message in the UI state.
     */
    fun shownMessage() {
        notifyChange(null)
    }

    /**
     * Updates the notification message in the UI state.
     *
     * @param notify The new notification message, or null to clear.
     */
    private fun notifyChange(notify: String?) {
        _state.update { old -> old.copy(notify = notify) }
    }
}