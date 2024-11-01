package bg.zahov.app.data.interfaces

import bg.zahov.app.data.local.Settings
import bg.zahov.app.data.model.state.TypeSettings
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.flow.Flow

interface SettingsProvider {
    suspend fun getSettings(): Flow<ObjectChange<Settings>>
    suspend fun addSetting(type: TypeSettings, value: Any)
    suspend fun resetSettings()
}