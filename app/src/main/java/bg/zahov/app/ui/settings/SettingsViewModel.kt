package bg.zahov.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.local.Settings
import bg.zahov.app.getSettingsProvider
import bg.zahov.app.getUserProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.fitness.app.R
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo by lazy {
        application.getSettingsProvider()
    }

    private val auth by lazy {
        application.getUserProvider()
    }

    private val workoutState by lazy {
        application.getWorkoutStateManager()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            repo.getSettings().collect {
                it.obj?.let { settings -> _state.postValue(State.Data(settings)) }
            }
        }
    }

    fun writeNewSetting(title: String, newValue: Any) {
        viewModelScope.launch {
            repo.addSetting(title, newValue)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            repo.resetSettings()
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.logout()
            workoutState.cancel()
            _state.postValue(State.Navigate(R.id.settings_to_welcome))
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            workoutState.cancel()
            auth.deleteAccount()
            _state.postValue(State.Navigate(R.id.settings_to_welcome))
        }
    }

    sealed interface State {
        data class Data(val data: Settings) : State
        data class Navigate(val action: Int?) : State

    }
}