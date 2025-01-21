package bg.zahov.app.ui.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import kotlinx.coroutines.flow.first
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

    /**
     * Executes the startup logic.
     *
     * This function is responsible for:
     * - Checking the user's authentication status.
     * - Initializing data sources and fetching user data if the user is authenticated.
     * - Navigating to the home screen if authentication is successful.
     * - Navigating to the welcome screen if the user is not authenticated or a `NoSuchElementException` occurs.
     * - Handling unexpected errors by initiating the service error countdown.
     *
     * @param pass A lambda function to be executed when loading is successful and the app should
     *             navigate to the home screen.
     * @param failed A lambda function to be executed when loading fails or when the app should
     *               navigate to the welcome screen.
     */
    fun loading(pass: () -> Unit, failed: () -> Unit) {
        viewModelScope.launch {
            try {
                if (userProvider.isAuthenticated()) {
                    userProvider.initDataSources()
                    userProvider.getUser().first()
                    pass()
                } else {
                    failed()
                }
            } catch (e: Exception) {
                when (e) {
                    is NoSuchElementException -> failed()
                    else -> serviceError.initiateCountdown()
                }
            }
        }
    }
}