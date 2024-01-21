package bg.zahov.app.data.repository

import bg.zahov.app.data.local.RealmManager
import bg.zahov.app.data.local.Settings
import bg.zahov.app.data.interfaces.SettingsRepository
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl : SettingsRepository {
    companion object {
        @Volatile
        private var instance: SettingsRepositoryImpl? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SettingsRepositoryImpl().also { instance = it } }
    }

    private val realm = RealmManager.getInstance()

    //FILTER INFORMATION HERE + CACHE

    //REMOVE !!
    override suspend fun getSettings(): Flow<ObjectChange<Settings>> = realm.getSettings()!!

    override suspend fun addSetting(value: Any) {
    }

    override suspend fun resetSettings() {
        //TODO()
    }
}