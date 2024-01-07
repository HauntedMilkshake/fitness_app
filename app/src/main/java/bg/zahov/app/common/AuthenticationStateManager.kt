package bg.zahov.app.common

class AuthenticationStateManager {
    private val observers = mutableListOf<AuthenticationStateObserver>()

    fun addObserver(observer: AuthenticationStateObserver){ observers.add(observer) }
    fun removeObserver(observer: AuthenticationStateObserver) { observers.remove(observer) }

    fun sendUpdateEvent(isAuthenticated: Boolean) {
        observers.forEach { it.onAuthenticationStateChanged(isAuthenticated) }
    }
}