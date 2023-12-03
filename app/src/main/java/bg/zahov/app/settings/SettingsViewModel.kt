package bg.zahov.app.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.realm_db.Settings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo: UserRepository = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> get() = _settings
    fun logout(){
        auth.signOut()
    }
    init{
        getSettings()
    }
    fun writeNewSetting(title: String, newValue: Any){
        viewModelScope.launch {
            repo.writeNewSettings(title, newValue)
        }
    }
    fun getSettings() {
        viewModelScope.launch {
            _settings.value = repo.getUserSettings()
        }
    }
//    fun refreshSettings(){
//        viewModelScope.launch {
//            repo.refreshSettings()
//        }
//    }
}