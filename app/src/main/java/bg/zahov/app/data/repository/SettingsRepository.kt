package bg.zahov.app.data.repository

import bg.zahov.app.data.local.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Flow<Settings>
}