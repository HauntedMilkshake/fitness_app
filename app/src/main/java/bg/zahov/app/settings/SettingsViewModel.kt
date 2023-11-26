package bg.zahov.app.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bg.zahov.app.data.Language
import bg.zahov.app.data.Settings
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Units
import bg.zahov.app.mediators.SettingsManager
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val settingsManager = SettingsManager.getInstance(application)
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> get() = _settings
    fun logout(){
        auth.signOut()
    }
    init{
        initSettings()
    }


    //TODO(add cases for settings with switch)
    fun updateSettings(title: String, newValue: Any){
        when(title){
            "Language" -> {
                if (newValue is Language) {
                    settingsManager.setLanguage(newValue)
                }
            }
            "Weight" -> {
                if (newValue is Units) {
                    settingsManager.setWeight(newValue)
                }
            }
            "Distance" -> {
                if (newValue is Units) {
                    settingsManager.setDistance(newValue)
                }
            }
            "Timer increment value" -> {
                if (newValue is Long) {
                    settingsManager.setRestTimer(newValue)
                }
            }
            "Sound" -> {
                if (newValue is Sound) {
                    settingsManager.setSoundSettings(newValue)
                }
            }
            else -> null
        }
    }
    private fun initSettings() {
        _settings.value = settingsManager.getSettings()
        Log.d("Initial Settings", _settings.value.toString())
    }
}