package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.ServiceState
import kotlinx.coroutines.flow.Flow

interface ServiceErrorHandler {
    suspend fun stopApplication()
    suspend fun observeServiceState(): Flow<ServiceState>
    suspend fun startCountdownTimer()
}