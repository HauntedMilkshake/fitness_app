package bg.zahov.app.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo: UserRepository = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> get() = _settings
    fun logout() {
        auth.signOut()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getSettings()
        }
    }

    fun writeNewSetting(title: String, newValue: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.writeNewSettings(title, newValue)
            updateLiveData(title, newValue, _settings)

        }
    }

    private suspend fun getSettings() {
        _settings.postValue(repo.getUserSettings())
    }

    fun resetSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.resetSettings()
        }
    }

    private fun updateLiveData(title: String, newValue: Any, data: MutableLiveData<Settings>) {
        val updatedSettings = data.value!!

        when (title) {
            "Language" -> {
                if (newValue is String) {
                    updatedSettings.language = Language.valueOf(newValue).name
                }
            }

            "Units" -> {
                if (newValue is String) {
                    updatedSettings.weight = Units.valueOf(newValue).name
                    updatedSettings.distance = Units.valueOf(newValue).name
                }
            }

            "Sound effects" -> {
                if (newValue is Boolean) {
                    updatedSettings.soundEffects = newValue
                }
            }

            "Theme" -> {
                if (newValue is String) {
                    updatedSettings.theme = Theme.valueOf(newValue).name
                }
            }

            "Timer increment value" -> {
                if (newValue is Int) {
                    updatedSettings.restTimer = newValue
                }
            }

            "Vibrate upon finish" -> {
                if (newValue is Boolean) {
                    updatedSettings.vibration = newValue
                }
            }

            "Sound" -> {
                if (newValue is String) {
                    updatedSettings.soundSettings = Sound.valueOf(newValue).name
                }
            }

            "Show update template" -> {
                if (newValue is Boolean) {
                    updatedSettings.updateTemplate = newValue
                }
            }

            "Use samsung watch during workout" -> {
                if (newValue is Boolean) {
                    updatedSettings.fit = newValue
                }
            }

            else -> return
        }

        data.postValue(updatedSettings)
    }


}
