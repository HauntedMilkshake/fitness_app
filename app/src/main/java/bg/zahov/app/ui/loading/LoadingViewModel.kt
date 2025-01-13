package bg.zahov.app.ui.loading

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
class LoadingViewModel(
    private val userProvider: UserProvider = Inject.userProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    private val _loading = MutableStateFlow(R.id.loading_to_welcome)

    val loading: StateFlow<Int> = _loading

    init {
        viewModelScope.launch {
            try {
                userProvider.authStateFlow().collect {
                    Log.d("auth", it.toString())
                    if (it) {
                        userProvider.initDataSources()
                        userProvider.getUser().first()
                        _loading.update { R.id.loading_to_home}
                    } else {
                        _loading.update { R.id.loading_to_welcome }
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is NoSuchElementException -> _loading.update { R.id.loading_to_welcome }
                    else -> serviceError.initiateCountdown()
                }
            }
        }
    }
}