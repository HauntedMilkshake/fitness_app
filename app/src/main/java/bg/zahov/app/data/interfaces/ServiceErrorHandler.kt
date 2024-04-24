package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.ServiceState
import kotlinx.coroutines.flow.Flow

interface ServiceErrorHandler {
    suspend fun initiateCountdown()
    suspend fun observeServiceState(): Flow<ServiceState>
    suspend fun stopApplication()
}