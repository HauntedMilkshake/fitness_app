package bg.zahov.app.mediators

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import bg.zahov.app.data.*
import java.util.concurrent.TimeUnit

class SettingsManager(application: Application) {
    companion object {
        const val PERSISTENCE_STORE_NAME = "UserSettings"
        const val PERSISTENCE_LANGUAGE = "Language"
        const val PERSISTENCE_WEIGHT = "Weight"
        const val PERSISTENCE_DISTANCE = "Distance"
        const val PERSISTENCE_SOUND_EFFECTS = "SoundEffects"
        const val PERSISTENCE_THEME = "Theme"
        const val PERSISTENCE_REST_TIMER = "RestTimer"
        const val PERSISTENCE_VIBRATION = "Vibration"
        const val PERSISTENCE_SOUND_SETTINGS = "SoundSettings"
        const val PERSISTENCE_UPDATE_TEMPLATE = "UpdateTemplate"
        const val PERSISTENCE_SYNC = "Sync"
        const val PERSISTENCE_FIT = "Fit"

        @Volatile
        private var settingsInstance: SettingsManager? = null
        fun getInstance(application: Application) = settingsInstance ?: synchronized(this) {
            settingsInstance ?: SettingsManager(application).also { settingsInstance = it }
        }
    }

    private val sharedPreferences = application.getSharedPreferences(PERSISTENCE_STORE_NAME, Context.MODE_PRIVATE)

    private val _language = MutableLiveData(Language.valueOf(sharedPreferences.getString(PERSISTENCE_LANGUAGE, Language.English.name) ?: Language.English.name))

    private val _weight = MutableLiveData(Units.valueOf(sharedPreferences.getString(PERSISTENCE_WEIGHT, Units.Normal.name) ?: Units.Normal.name))

    private val _distance = MutableLiveData(Units.valueOf(sharedPreferences.getString(PERSISTENCE_DISTANCE, Units.Normal.name) ?: Units.Normal.name))

    private val _soundEffects = MutableLiveData(sharedPreferences.getBoolean(PERSISTENCE_SOUND_EFFECTS, true))

    private val _theme = MutableLiveData(Theme.valueOf(sharedPreferences.getString(PERSISTENCE_THEME, Theme.Dark.name) ?: Theme.Dark.name))

    private val _restTimer = MutableLiveData((sharedPreferences.getInt(PERSISTENCE_REST_TIMER, 30)))

    private val _vibration = MutableLiveData(sharedPreferences.getBoolean(PERSISTENCE_VIBRATION, true))

    private val _soundSettings = MutableLiveData(Sound.valueOf(sharedPreferences.getString(PERSISTENCE_SOUND_SETTINGS, Sound.SOUND_1.name) ?: Sound.SOUND_1.name))

    private val _updateTemplate = MutableLiveData(sharedPreferences.getBoolean(PERSISTENCE_UPDATE_TEMPLATE, true))

    private val _sync = MutableLiveData(sharedPreferences.getBoolean(PERSISTENCE_SYNC, true))

    private val _fit = MutableLiveData(sharedPreferences.getBoolean(PERSISTENCE_FIT, false))

    fun getSettings(): Settings{
        return Settings(
            language = Language.valueOf(sharedPreferences.getString(PERSISTENCE_LANGUAGE, Language.English.name) ?: Language.English.name),
            weight = Units.valueOf(sharedPreferences.getString(PERSISTENCE_WEIGHT, Units.Normal.name) ?: Units.Normal.name),
            distance = Units.valueOf(sharedPreferences.getString(PERSISTENCE_DISTANCE, Units.Normal.name) ?: Units.Normal.name),
            soundEffects = sharedPreferences.getBoolean(PERSISTENCE_SOUND_EFFECTS, true),
            theme = Theme.valueOf(sharedPreferences.getString(PERSISTENCE_THEME, Theme.Dark.name) ?: Theme.Dark.name),
            restTimer = sharedPreferences.getInt(PERSISTENCE_REST_TIMER, 30),
            vibration = sharedPreferences.getBoolean(PERSISTENCE_VIBRATION, true),
            soundSettings = Sound.valueOf(sharedPreferences.getString(PERSISTENCE_SOUND_SETTINGS, Sound.SOUND_1.name) ?: Sound.SOUND_1.name),
            updateTemplate = sharedPreferences.getBoolean(PERSISTENCE_UPDATE_TEMPLATE, true),
            sync = sharedPreferences.getBoolean(PERSISTENCE_SYNC, true),
            fit = sharedPreferences.getBoolean(PERSISTENCE_FIT, false)
        )
    }
    fun setLanguage(language: Language) {
        sharedPreferences.edit().putString(PERSISTENCE_LANGUAGE, language.name).apply()
        _language.value = language
        Log.d("New Language", _language.value.toString())
    }

    fun setWeight(weight: Units) {
        sharedPreferences.edit().putString(PERSISTENCE_WEIGHT, weight.name).apply()
        _weight.value = weight
    }

    fun setDistance(distance: Units) {
        sharedPreferences.edit().putString(PERSISTENCE_DISTANCE, distance.name).apply()
        _distance.value = distance
    }

    fun setSoundEffects(enable: Boolean) {
        sharedPreferences.edit().putBoolean(PERSISTENCE_SOUND_EFFECTS, enable).apply()
        _soundEffects.value = enable
    }

    fun setTheme(theme: Theme) {
        sharedPreferences.edit().putString(PERSISTENCE_THEME, theme.name).apply()
        _theme.value = theme
    }

    fun setRestTimer(seconds: Int) {
        sharedPreferences.edit().putInt(PERSISTENCE_REST_TIMER, seconds).apply()
        _restTimer.value = seconds
    }

    fun setVibration(enable: Boolean) {
        sharedPreferences.edit().putBoolean(PERSISTENCE_VIBRATION, enable).apply()
        _vibration.value = enable
    }

    fun setSoundSettings(sound: Sound) {
        sharedPreferences.edit().putString(PERSISTENCE_SOUND_SETTINGS, sound.name).apply()
        _soundSettings.value = sound
    }

    fun setUpdateTemplate(enable: Boolean) {
        sharedPreferences.edit().putBoolean(PERSISTENCE_UPDATE_TEMPLATE, enable).apply()
        _updateTemplate.value = enable
    }

    fun setSync(enable: Boolean) {
        sharedPreferences.edit().putBoolean(PERSISTENCE_SYNC, enable).apply()
        _sync.value = enable
    }

    fun setFit(enable: Boolean) {
        sharedPreferences.edit().putBoolean(PERSISTENCE_FIT, enable).apply()
        _fit.value = enable
    }
    fun resetSettings(){
        setLanguage(Language.English)
        setWeight(Units.Normal)
        setDistance(Units.Normal)
        setSoundEffects(true)
        setTheme(Theme.Dark)
        setRestTimer(30)
        setVibration(true)
        setSoundSettings(Sound.SOUND_1)
        setUpdateTemplate(true)
        setSync(true)
        setFit(false)
    }
}