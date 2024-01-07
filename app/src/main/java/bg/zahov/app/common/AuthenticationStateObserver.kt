package bg.zahov.app.common

interface AuthenticationStateObserver {
    fun onAuthenticationStateChanged(isAuthenticated: Boolean)
}