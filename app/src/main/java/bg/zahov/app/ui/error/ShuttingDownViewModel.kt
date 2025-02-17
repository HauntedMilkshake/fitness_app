package bg.zahov.app.ui.error

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the countdown state during application shutdown.
 * It handles updating the countdown timer and triggers the application shutdown when the countdown reaches zero.
 *
 * @property serviceErrorHandler ServiceErrorHandler used to stop the application when countdown ends.
 */
@HiltViewModel
class ShuttingDownViewModel @Inject constructor(private val serviceErrorHandler: ServiceErrorHandler) :
    ViewModel() {
    //The current countdown state, which starts at 5 seconds.
    private val _state = MutableStateFlow<Int>(5)

    /**
     * Exposes the current countdown state as a [StateFlow].
     */
    val state: StateFlow<Int> = _state

    init {
        viewModelScope.launch {
            while (_state.value > 0) {
                delay(1000)
                _state.update { old -> old - 1 }
            }
            serviceErrorHandler.stopApplication()

        }
    }
}