package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.model.state.TypeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Implementation of [SettingsProvider] that manages access to user settings.
 *
 * This singleton class provides methods to:
 * - Retrieve current settings.
 * - Add or update specific settings.
 * - Reset settings to their default values.
 *
 */
class SettingsProviderImpl @Inject constructor() : SettingsProvider {
    /**
     * Retrieves the current settings as a flow, observing changes in real-time.
     *
     */
    override suspend fun <T> getSettings(): Flow<T> {
        return flowOf()
        //TODO()
    }

    /**
     * Adds or updates a setting with the specified title and value.
     *
     * @param type The type of the setting to be updated.
     * @param value The new value to set.
     */
    override suspend fun addSetting(
        type: TypeSettings,
        value: Any,
    ) {
    } //= settingsRepository.addSetting(type, value)

    /**
     * Resets all settings to their default values.
     */
    override suspend fun resetSettings() {} // = settingsRepository.resetSettings()
}