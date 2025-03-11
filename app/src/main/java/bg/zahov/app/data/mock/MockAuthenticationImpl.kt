package bg.zahov.app.data.mock

import bg.zahov.app.data.interfaces.AuthResponse
import bg.zahov.app.data.interfaces.Authentication
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

class MockAuthenticationImpl : Authentication {
    companion object {
        private var instance: MockAuthenticationImpl? = null
        fun getInstance() = instance ?: MockAuthenticationImpl().also { instance = it }
    }

    private val isAuthenticated = AtomicBoolean(true)
    private val userEmail = MutableStateFlow<String?>("mock@example.com")
    private val userId = MutableStateFlow<String?>("mockUserId")

    override suspend fun login(email: String, password: String): AuthResponse {
        isAuthenticated.set(true)
        userEmail.value = email
        userId.value = "mockUserId"
        return AuthResponse.Success(userId = "mockUserId")
    }

    override suspend fun signup(email: String, password: String): AuthResponse {
        isAuthenticated.set(true)
        userEmail.value = email
        userId.value = "mockUserId"
        return AuthResponse.Success(userId = "mockUserId")
    }

    override suspend fun logout() {
        isAuthenticated.set(false)
        userEmail.value = null
        userId.value = null
    }

    override suspend fun deleteAccount() {
        isAuthenticated.set(false)
        userEmail.value = null
        userId.value = null
    }

    override suspend fun passwordResetByEmail(email: String): Task<Void> = Tasks.forResult(null)

    override suspend fun passwordResetForLoggedUser(): Task<Void> = Tasks.forResult(null)

    override fun authStateFlow(): StateFlow<Boolean> = MutableStateFlow(isAuthenticated.get())

    override suspend fun initDataSources() {
        println("Initializing mock data sources")
    }

    override suspend fun createDataSources(username: String, userId: String) {
        println("Creating mock data sources for user: $username with ID: $userId")
    }

    override suspend fun updatePassword(newPassword: String): Task<Void> = Tasks.forResult(null)

    override suspend fun reauthenticate(password: String): Task<Void> = Tasks.forResult(null)

    override suspend fun getEmail(): String = userEmail.value ?: "mock@example.com"
}
