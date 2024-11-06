package bg.zahov.app.ui.settings.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.app.data.model.state.EditProfileData
import bg.zahov.app.data.model.ToastManager
import bg.zahov.fitness.app.R
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
    private val errorHandler: ServiceErrorHandler = Inject.serviceErrorHandler,
    private val toastManager: ToastManager = ToastManager
) : ViewModel() {
    private val _state = MutableStateFlow(EditProfileData())
    val state: StateFlow<EditProfileData> = _state

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
        _state.update { it.copy(passwordVisibilityDialog = _state.value.passwordVisibility.not()) }
    }

    /**
     * Updates the password for the popup in the UI state.
     *
     * @param password The new password to be set.
     */
    fun onPasswordChangeDialog(password: String) {
        _state.update { it.copy(passwordDialog = password) }
    }

    /**
     * Toggles the visibility of the password field in the popup.
     */
    fun onPasswordVisibilityChangeDialog() {
        _state.update { it.copy(passwordVisibility = _state.value.passwordVisibility.not()) }
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
                        old.copy(username = newUsername)
                    }
                    toastManager.showToast(R.string.update_username_success)
                }
                .addOnFailureListener { toastManager.showToast(R.string.update_username_fail) }
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
                        old.copy(authenticated = true)
                    }
                    toastManager.showToast(R.string.re_authenticate_success)
                }
                .addOnFailureListener { toastManager.showToast(R.string.re_authenticate_fail) }
        }
    }

    /**
     * Sends a password reset link to the user's registered email address.
     */
    fun sendPasswordResetLink() {
        viewModelScope.launch {
            repo.passwordResetForLoggedUser()
                .addOnSuccessListener { toastManager.showToast(R.string.reset_password_success) }
                .addOnFailureListener { toastManager.showToast(R.string.reset_password_fail) }
        }
    }

    /**
     * Updates the user's password with the new password stored in the UI state.
     */
    fun updatePassword() {
        val newPassword = _state.value.password
        viewModelScope.launch {
            repo.updatePassword(newPassword)
                .addOnSuccessListener { toastManager.showToast(R.string.update_password_success) }
                .addOnFailureListener { toastManager.showToast(R.string.update_password_fail) }
        }
    }
}