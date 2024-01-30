package bg.zahov.app.data.model

import bg.zahov.app.ui.settings.profile.EditProfileViewModel

data class EditProfileUiModel (
    val isUnlocked: Boolean = false,
    val errorMessage: String? = null,
    val username: String = "",
    val email: String = "",
    val notifyMessage: String? = null
)
//TODO(FIX)
object EditProfileUiMapper{
    fun map(state: EditProfileViewModel.State) = when(state) {
        EditProfileViewModel.State.Default -> EditProfileUiModel()
        is EditProfileViewModel.State.Email -> EditProfileUiModel(email = state.email)
        is EditProfileViewModel.State.Error -> EditProfileUiModel(errorMessage = state.error)
        is EditProfileViewModel.State.Notify -> EditProfileUiModel(notifyMessage = state.message)
        is EditProfileViewModel.State.Unlocked -> EditProfileUiModel(isUnlocked = state.isUnlocked)
        is EditProfileViewModel.State.Username -> EditProfileUiModel(username = state.username)
    }

}
