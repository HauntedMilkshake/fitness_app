package bg.zahov.app.data.local

import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.LanguageKeys
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.SoundKeys
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.ThemeKeys
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.UnitsKeys
import io.realm.kotlin.types.RealmObject

class Settings : RealmObject {
    var language: String = Language.fromKey(LanguageKeys.ENGLISH)
    var units: String = Units.fromKey(UnitsKeys.METRIC)
    var soundEffects: Boolean = true
    var theme: String = Theme.fromKey(ThemeKeys.DARK)
    var restTimer: Int = 30
    var vibration: Boolean = true
    var soundSettings: String = Sound.fromKey(SoundKeys.SOUND_1)
    var updateTemplate: Boolean = true
    var fit: Boolean = false
    var automaticSync: Boolean = true
}