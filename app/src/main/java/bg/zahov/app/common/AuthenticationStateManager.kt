package bg.zahov.app.common

class AuthenticationStateManager {
    companion object {
        @Volatile
        private var instance: AuthenticationStateManager? = null
        fun getInstance() =
            instance ?: synchronized(this){
                instance ?: AuthenticationStateManager().also { instance = it }
            }
    }
    private val observers = mutableListOf<AuthenticationStateObserver>()
    fun addObserver(observer: AuthenticationStateObserver){ observers.add(observer) }
    fun removeObserver(observer: AuthenticationStateObserver) { observers.remove(observer) }
    fun sendUpdateEvent(isAuthenticated: Boolean) {
        observers.forEach { it.onAuthenticationStateChanged(isAuthenticated) }
    }
}