package bg.zahov.app.data.model.state

import bg.zahov.app.ui.settings.profile.EditProfileViewModel

data class EditProfileUiModel (
    val errorMessage: String? = null,
    val notifyMessage: String? = null,
    val shutdown: Boolean = false
)
//TODO(FIX)
object EditProfileUiMapper{
    fun map(state: EditProfileViewModel.State) = when(state) {
        is EditProfileViewModel.State.Notify -> EditProfileUiModel(notifyMessage = state.message)
        is EditProfileViewModel.State.Error -> EditProfileUiModel(errorMessage = state.error, shutdown = state.shutdown)
    }

}
