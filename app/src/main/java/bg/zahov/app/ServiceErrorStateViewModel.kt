package bg.zahov.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ServiceState
import bg.zahov.fitness.app.R
import kotlinx.coroutines.launch

class ServiceErrorStateViewModel(application: Application) : AndroidViewModel(application) {
    private val serviceErrorHandler by lazy {
        application.getServiceErrorProvider()
    }
    private val _serviceState = MutableLiveData<State>()
    val serviceState: LiveData<State>
        get() = _serviceState

    init {
        viewModelScope.launch {
            serviceErrorHandler.observeServiceState().collect {
                when (it) {
                    ServiceState.Unavailable -> _serviceState.postValue(State.NavigateToTimer(action = R.id.to_shutting_down_fragment))
                    ServiceState.Shutdown -> _serviceState.postValue(State.Shutdown(true))
                    else -> {}
                }
            }
        }
    }

    sealed interface State {
        data class NavigateToTimer(val action: Int) : State
        data class Shutdown(val shouldShutdown: Boolean) : State
    }
}