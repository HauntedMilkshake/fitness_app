package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.local.RealmManager
import bg.zahov.app.data.local.Settings
import bg.zahov.app.data.model.state.TypeSettings
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.flow.Flow

/**
 * Repository implementation for managing user settings data.
 *
 * This singleton class interacts with the database layer via [RealmManager]
 * to perform actions such as retrieving, updating, and resetting settings.
 *
 * @property realm Handles database operations related to settings through [RealmManager].
 */
class SettingsRepositoryImpl : SettingsProvider {

    companion object {
        @Volatile
        private var instance: SettingsRepositoryImpl? = null

        /**
         * Provides a thread-safe singleton instance of [SettingsRepositoryImpl].
         *
         * @return The singleton instance of [SettingsRepositoryImpl].
         */
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SettingsRepositoryImpl().also { instance = it }
            }
    }

    private val realm = RealmManager.getInstance()

    /**
     * Retrieves the current settings as a [Flow], allowing real-time updates.
     *
     * @return A [Flow] emitting [ObjectChange] events for [Settings] objects.
     */
    override suspend fun getSettings(): Flow<ObjectChange<Settings>> = realm.getSettings()

    /**
     * Adds or updates a specific setting.
     *
     * @param type The type of the setting.
     * @param value The value to be set for the specified setting.
     */
    override suspend fun addSetting(type: TypeSettings, value: Any) {
        realm.updateSetting(type, value)
    }

    /**
     * Resets all settings to their default values.
     */
    override suspend fun resetSettings() {
        realm.resetSettings()
    }
}
