package bg.zahov.app.data.model.state

/**
 * UI state for EditProfileViewModel, containing properties related to authentication,
 * user credentials, password visibility, and notifications.
 *
 * @property authenticated Whether the user is authenticated.
 * @property username The current username.
 * @property password The current password.
 * @property passwordVisibility Controls the visibility of the password.
 * @property passwordDialog The current password for the popup.
 * @property passwordVisibility Controls the visibility of the password for the popup.
 */
data class EditProfileData(
    val authenticated: Boolean = false,
    val username: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false,
    val passwordDialog: String = "",
    val passwordVisibilityDialog: Boolean = false,
)