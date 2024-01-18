package bg.zahov.app.data

import bg.zahov.app.data.local.RealmManager

class SettingsRepository {
    companion object {
        @Volatile
        private var instance: SettingsRepository? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SettingsRepository().also { instance = it } }
    }

    private val realm = RealmManager.getInstance()

    suspend fun getSettings() = realm.getSettings()

    suspend fun getSettingsSync() = realm.getSettingsSync()

    suspend fun updateSetting(title: String, newValue: Any) = realm.updateSetting(title, newValue)
    suspend fun resetSettings() = realm.resetSettings()
}