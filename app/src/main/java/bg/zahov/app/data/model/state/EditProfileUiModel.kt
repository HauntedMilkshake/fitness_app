package bg.zahov.app.data.model.state

import bg.zahov.app.ui.settings.profile.EditProfileViewModel

data class EditProfileUiModel (
    val notifyMessage: String? = null,
)
object EditProfileUiMapper{
    fun map(state: EditProfileViewModel.State) = when(state) {
        is EditProfileViewModel.State.Notify -> EditProfileUiModel(notifyMessage = state.message)
        EditProfileViewModel.State.Default -> EditProfileUiModel()
    }

}
