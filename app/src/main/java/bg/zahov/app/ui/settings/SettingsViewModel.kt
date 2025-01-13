package bg.zahov.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.app.data.interfaces.WorkoutActions
import bg.zahov.app.data.model.state.TypeSettings
import bg.zahov.app.data.provider.UserProviderImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing app settings.
 *
 * @property repo Provides access to the user's settings via [SettingsProviderImpl].
 * @property auth Manages user authentication and provides user actions via [UserProviderImpl].
 * @property workoutState Manages the current workout state, ensuring any ongoing workout is tracked and can be cancelled on user logout or account deletion.
 *
 * This ViewModel handles:
 * - Retrieving and updating user settings.
 * - Logging out the user and resetting their settings.
 * - Deleting the user's account and associated data.
 */
class SettingsViewModel(
    private val repo: SettingsProvider = Inject.settingsProvider,
    private val auth: UserProvider = Inject.userProvider,
    private val workoutState: WorkoutActions = Inject.workoutState,
) : ViewModel() {

    /**
     * Data class representing the state of the settings UI.
     *
     * @property data Current settings data, represented as a TODO() object
     * @property navigateBack A Boolean flag indicating whether to navigate back. Defaults to false.
     */
    data class SettingsData(val data: String, val navigateBack: Boolean = false)

    // Holds the current UI state, updated whenever settings data changes
    private val _uiState = MutableStateFlow(SettingsData(data = ""))
    val uiState: StateFlow<SettingsData> = _uiState

    init {
        // Initializes settings by collecting data from repo
        viewModelScope.launch {
//            repo.getSettings().collect {
//                it.obj?.let { settings ->
//                    _uiState.update { old -> old.copy(data = settings) }
//                }
//            }
        }
    }

    /**
     * Updates a specific setting with a new value.
     *
     * @param type The type of the setting to be updated.
     * @param newValue The new value for the specified setting.
     */
    fun writeNewSetting(type: TypeSettings, newValue: Any) {
        viewModelScope.launch {
            repo.addSetting(type, newValue)
        }
    }

    /**
     * Resets all user settings to their default values.
     */
    fun resetSettings() {
        viewModelScope.launch {
            repo.resetSettings()
        }
    }

    /**
     * Logs out the current user, cancels ongoing workouts and navigates back to welcome.
     */
    fun logout() {
        viewModelScope.launch {
            auth.logout()
            workoutState.cancel()
            _uiState.update { old -> old.copy(navigateBack = true) }
        }
    }

    /**
     * Deletes the user's account, cancels ongoing workouts and navigates back to welcome.
     */
    fun deleteAccount() {
        viewModelScope.launch {
            workoutState.cancel()
            auth.deleteAccount()
            _uiState.update { old -> old.copy(navigateBack = true) }
        }
    }
}
