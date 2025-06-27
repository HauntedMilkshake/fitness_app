package bg.zahov.app.data.repository.mock

import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.model.ServiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MockServiceErrorHandler : ServiceErrorHandler {

    private val serviceState = MutableStateFlow<ServiceState>(ServiceState.Available)
    override suspend fun initiateCountdown() {}

    override suspend fun observeServiceState(): Flow<ServiceState> = serviceState

    override suspend fun stopApplication() {}
}