package bg.zahov.app.util

interface SettingsChangeListener {
    fun onSettingChanged(title: String, newValue: Any)
}