package bg.zahov.app.data.model.state

/**
 * UI state for EditProfileViewModel, containing properties related to authentication,
 * user credentials, password visibility, and notifications.
 *
 * @property authenticated Whether the user is authenticated.
 * @property username The current username.
 * @property password The current password.
 * @property passwordVisibility Controls the visibility of the password.
 */
data class EditProfileData(
    val authenticated: Boolean = false,
    val username: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false,
)