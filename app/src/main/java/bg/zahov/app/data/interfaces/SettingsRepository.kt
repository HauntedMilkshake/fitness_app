package bg.zahov.app.data.interfaces

import bg.zahov.app.data.local.Settings
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Flow<ObjectChange<Settings>>
    suspend fun addSetting(title: String, value: Any)
    suspend fun resetSettings()
}