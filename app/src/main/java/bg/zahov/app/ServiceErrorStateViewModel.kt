package bg.zahov.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.model.ServiceState
import bg.zahov.app.data.model.state.ShutDownData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel that manages the service error state and updates the UI based on the service status.
 * It listens to changes in the service state and updates the internal state accordingly.
 *
 * @property serviceErrorHandler The ServiceErrorHandler used to observe service state changes.
 */
class ServiceErrorStateViewModel(
    private val serviceErrorHandler: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    //The state contains flags for whether the service is shutting down or needs to navigate to shutting down screen.
    private val _serviceState = MutableStateFlow(ShutDownData())

    /**
     * Exposes the current shutdown state as a [StateFlow].
     * Observers can collect this to react to state changes.
     */
    val serviceState: StateFlow<ShutDownData> = _serviceState

    /**
     * Initializes the ViewModel and starts observing the service state.
     * Based on the observed service state, the internal state is updated.
     */
    init {
        viewModelScope.launch {
            serviceErrorHandler.observeServiceState().collect { serviceState ->
                when (serviceState) {
                    ServiceState.Unavailable -> _serviceState.update {
                        it.copy(navigateToShuttingDown = true)
                    }

                    ServiceState.Shutdown -> _serviceState.update { it.copy(shutDown = true) }
                    else -> {}
                }
            }
        }
    }
}
