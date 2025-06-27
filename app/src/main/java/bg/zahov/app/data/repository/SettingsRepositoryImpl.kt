package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.model.state.TypeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Repository implementation for managing user settings data.
 *
 * This singleton class interacts with the database layer via TODO()
 * to perform actions such as retrieving, updating, and resetting settings.
 *
 */
class SettingsRepositoryImpl @Inject constructor() : SettingsProvider {

    /**
     * Retrieves the current settings as a [Flow], allowing real-time updates.
     */
    override suspend fun <T> getSettings(): Flow<T> {
        return flowOf()
//        TODO("Not yet implemented")
    }

    /**
     * Adds or updates a specific setting.
     *
     * @param type The type of the setting.
     * @param value The value to be set for the specified setting.
     */
    override suspend fun addSetting(type: TypeSettings, value: Any) {
    }

    /**
     * Resets all settings to their default values.
     */
    override suspend fun resetSettings() {
    }
}
