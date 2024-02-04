package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.local.Settings
import bg.zahov.app.data.repository.SettingsRepositoryImpl
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.flow.Flow

class SettingsProviderImpl: SettingsProvider {
    companion object {

        @Volatile
        private var instance: SettingsProviderImpl? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: SettingsProviderImpl().also { instance = it }
        }
    }

    private val settingsRepository = SettingsRepositoryImpl.getInstance()

    override suspend fun getSettings(): Flow<ObjectChange<Settings>> = settingsRepository.getSettings()

    override suspend fun addSetting(title: String, value: Any) = settingsRepository.addSetting(title, value)

    override suspend fun resetSettings() = settingsRepository.resetSettings()
}