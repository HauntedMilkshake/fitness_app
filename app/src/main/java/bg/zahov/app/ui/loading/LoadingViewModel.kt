package bg.zahov.app.ui.loading

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
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
     * Data class representing the state of the loading process.
     *
     * @property navigateHome Indicates whether the app should navigate to the home screen.
     * @property navigateWelcome Indicates whether the app should navigate to the welcome screen.
     */
    data class LoadingData(
        val navigateHome: Boolean = false,
        val navigateWelcome: Boolean = false
    )

    /**
     * Holds the current state of the loading process.
     */
    private val _uiState = MutableStateFlow(LoadingData())

    /**
     * Exposes the immutable state of the loading process for observers.
     */
    val uiState: StateFlow<LoadingData> = _uiState

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
    init {
        viewModelScope.launch {
            try {
                if (userProvider.isAuthenticated()) {
                    userProvider.initDataSources()
                    userProvider.getUser().first()
                    _uiState.update { old -> old.copy(navigateHome = true) }
                } else {
                    _uiState.update { old -> old.copy(navigateWelcome = true) }
                }
            } catch (e: Exception) {
                Log.d("error", e.toString())
                when (e) {
                    is NoSuchElementException -> _uiState.update { old -> old.copy(navigateWelcome = true) }
                    else -> serviceError.initiateCountdown()
                }
            }
            // Reset the loading state after handling navigation
            _uiState.update { LoadingData() }
        }
    }
}