package bg.zahov.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.local.Settings
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.data.repository.SettingsRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingsRepositoryImpl.getInstance()
    private val auth = AuthenticationImpl.getInstance()
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings>
        get() = _settings


    init {
        viewModelScope.launch {
            repo.getSettings()?.collect {
                _settings.postValue(it.obj)
            }
        }
    }

    fun writeNewSetting(title: String, newValue: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addSetting(title, newValue)
        }
    }

    fun resetSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.resetSettings()
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.logout()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            auth.deleteAccount()
        }
    }

}
