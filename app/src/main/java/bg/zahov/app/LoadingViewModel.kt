package bg.zahov.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.provider.UserProviderImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the app's loading process at startup.
 *
 * This ViewModel is designed to handle the logic required to determine the next step of the app:
 * whether to proceed to the home screen or display the welcome screen.
 * It also ensures robust error handling during the loading process.
 *
 * @property userProvider A service that provides user-related functionality, such as authentication
 *                        checks and user data initialization. Injected by default via `Inject.userProvider`.
 * @property serviceError A handler for managing errors and service-related issues during the startup
 *                        process. Injected by default via `Inject.serviceErrorHandler`.
 */
@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val userProvider: UserProviderImpl,
    private val serviceError: ServiceErrorHandler,
) : ViewModel() {
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _navigationTarget: MutableStateFlow<Any> =
        MutableStateFlow(Welcome)
    val navigationTarget: StateFlow<Any> = _navigationTarget

    init {
        viewModelScope.launch {
            try {
                userProvider.authStateFlow().collect { isAuthenticated ->
                    if (isAuthenticated) {
                        _loading.value = true
                        userProvider.initDataSources()
                        _navigationTarget.update { Home }
                    } else {
                        _navigationTarget.update { Welcome }
                    }
                    _loading.value = false
                }
            } catch (e: Exception) {
                when (e) {
                    is NoSuchElementException -> _navigationTarget.update { Welcome }
                    else -> serviceError.initiateCountdown()
                }
                _loading.value = false
            }
        }
    }
}