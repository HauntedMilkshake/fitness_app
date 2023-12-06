package bg.zahov.app.common

interface SettingsChangeListener {
    fun onSettingChanged(title: String, newValue: Any)
}