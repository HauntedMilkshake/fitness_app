package bg.zahov.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.local.Settings
import bg.zahov.app.data.provider.SettingsProviderImpl
import bg.zahov.app.data.provider.UserProviderImpl
import bg.zahov.app.data.provider.WorkoutStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repo: SettingsProviderImpl = Inject.settingsProvider,
    private val auth: UserProviderImpl = Inject.userProvider,
    private val workoutState: WorkoutStateManager = Inject.workoutState
) : ViewModel() {

    data class SettingsData(
        val data: Settings,
        val returnBack: Boolean = false
    )

    private val _uiState = MutableStateFlow(SettingsData(data = Settings()))
    val uiState: StateFlow<SettingsData> = _uiState

    init {
        viewModelScope.launch {
            repo.getSettings().collect {
                it.obj?.let { settings ->
                    _uiState.update { old -> old.copy(data = settings) }
                }
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
            _uiState.update { old -> old.copy(returnBack = true) }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            workoutState.cancel()
            auth.deleteAccount()
            _uiState.update { old -> old.copy(returnBack = true) }
        }
    }
}