package bg.zahov.app.data.model.state

import bg.zahov.app.data.local.Settings
import bg.zahov.app.ui.settings.SettingsViewModel

data class SettingsUiModel(
    val settings: Settings? = null,
    val action: Int? = null
)

object SettingsUiMapper {
    fun map(state: SettingsViewModel.State) = when(state) {
        is SettingsViewModel.State.Navigate -> SettingsUiModel(action = state.action)
        is SettingsViewModel.State.Data -> SettingsUiModel(settings = state.data)
    }
}