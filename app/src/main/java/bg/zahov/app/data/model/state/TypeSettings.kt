package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.Units

enum class TypeSettings(val value: String, val list: List<Any>) {
    // Enums representing various settings keys.
    LANGUAGE_SETTING("Language Settings", Language.entries.map { it.key }),
    UNIT_SETTING("Units Settings", Units.entries.map { it.key }),
    SOUND_EFFECTS_SETTING("soundEffectsSettings", listOf(true, false)),
    THEME_SETTING("Theme Settings", Theme.entries.map { it.key }),
    REST_TIMER_SETTING("Reset Timer Settings", listOf(5, 15, 30)),
    VIBRATION_SETTING("vibrateSettings", listOf(true, false)),
    SOUND_SETTING("Sound Settings", Sound.entries.map { it.key }),
    UPDATE_TEMPLATE_SETTING("Update Template Settings", listOf(true, false)),
    WATCH_SETTINGS("Watch Settings", listOf(true, false)),
    AUTOMATIC_SYNC_SETTING("Auto Sync Settings", listOf(true, false))
}