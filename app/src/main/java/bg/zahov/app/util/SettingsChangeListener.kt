package bg.zahov.app.util

import bg.zahov.app.data.model.state.TypeSettings

interface SettingsChangeListener {
    fun onSettingChanged(type: TypeSettings, newValue: Any)
}