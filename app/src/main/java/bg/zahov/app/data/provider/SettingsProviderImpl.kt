package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.local.Settings
import bg.zahov.app.data.repository.SettingsRepositoryImpl
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [SettingsProvider] that manages access to user settings.
 *
 * This singleton class provides methods to:
 * - Retrieve current settings.
 * - Add or update specific settings.
 * - Reset settings to their default values.
 *
 * @property settingsRepository Manages the actual data storage and retrieval via [SettingsRepositoryImpl].
 */
class SettingsProviderImpl: SettingsProvider {

    companion object {
        @Volatile
        private var instance: SettingsProviderImpl? = null

        /**
         * Retrieves the singleton instance of [SettingsProviderImpl],
         * creating it if necessary.
         *
         * @return The singleton instance of [SettingsProviderImpl].
         */
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: SettingsProviderImpl().also { instance = it }
        }
    }

    private val settingsRepository = SettingsRepositoryImpl.getInstance()

    /**
     * Retrieves the current settings as a flow, observing changes in real-time.
     *
     * @return A [Flow] of [ObjectChange] events for [Settings].
     */
    override suspend fun getSettings(): Flow<ObjectChange<Settings>> = settingsRepository.getSettings()

    /**
     * Adds or updates a setting with the specified title and value.
     *
     * @param title The title or key of the setting to be updated.
     * @param value The new value to set.
     */
    override suspend fun addSetting(title: String, value: Any) = settingsRepository.addSetting(title, value)

    /**
     * Resets all settings to their default values.
     */
    override suspend fun resetSettings() = settingsRepository.resetSettings()
}