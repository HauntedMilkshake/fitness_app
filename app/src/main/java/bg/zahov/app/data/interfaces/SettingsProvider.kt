package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.state.TypeSettings
import kotlinx.coroutines.flow.Flow

interface SettingsProvider {
    suspend fun <T> getSettings(): Flow<T>
    suspend fun addSetting(type: TypeSettings, value: Any)
    suspend fun resetSettings()
}