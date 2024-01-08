package bg.zahov.app.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.backend.Settings
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application){
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo: UserRepository = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> get() = _settings

    init {
        getSettings()
    }

    fun writeNewSetting(title: String, newValue: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateSetting(title, newValue)
        }
    }

    private fun getSettings() {
        viewModelScope.launch {
            repo.getSettings()?.collect {
                when (it) {
                    is DeletedObject -> _settings.postValue(Settings())
                    is InitialObject -> _settings.postValue(it.obj)
                    is UpdatedObject -> _settings.postValue(it.obj)
                }
            }
        }
    }

    fun resetSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.resetSettings()
        }
    }
}
