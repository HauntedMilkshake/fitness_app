package bg.zahov.app.settings

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
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
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
        }
    }

    private suspend fun getSettings() {
//        repo.getUserSettings()?.collect {
//            when(it){
//                is DeletedObject -> _settings.postValue(Settings())
//                is InitialObject -> _settings.postValue(it.obj)
//                is UpdatedObject -> _settings.postValue(it.obj)
//            }
//        }
    }

    fun resetSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.resetSettings()
        }
    }
}
