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
 * Automatically executes startup logic upon initialization to determine whether to
 * navigate to the home screen or the welcome screen.
 *
 * @property userProvider Provides user-related functionality, such as checking authentication
 *                        and initializing data sources.
 * @property serviceError Handles errors and service-related issues during the startup process.
 */
class LoadingViewModel(
    private val userProvider: UserProvider = Inject.userProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {
    /**
     * Initializes the startup logic.
     *
     * - Checks user authentication status.
     * - Initializes user data sources and fetches the user data if authenticated,
     *   then navigates to the home screen.
     * - Navigates to the welcome screen if the user is not authenticated or
     *   if a `NoSuchElementException` occurs.
     * - Handles other exceptions by triggering the service error countdown.
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