package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.model.ServiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ServiceErrorHandlerImpl : ServiceErrorHandler {
    companion object {
        @Volatile
        private var instance: ServiceErrorHandlerImpl? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ServiceErrorHandlerImpl().also { instance = it }
        }
    }

    private val _state = MutableStateFlow(ServiceState.Available)
    private val state: Flow<ServiceState>
        get() = _state

    override suspend fun stopApplication() {
        _state.value = ServiceState.Shutdown
    }

    override suspend fun observeServiceState(): Flow<ServiceState> = state
    override suspend fun startCountdownTimer() {
        TODO("Not yet implemented")
    }
}