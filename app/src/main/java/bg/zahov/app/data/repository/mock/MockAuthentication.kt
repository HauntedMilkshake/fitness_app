package bg.zahov.app.data.repository.mock

import bg.zahov.app.data.interfaces.AuthResponse
import bg.zahov.app.data.interfaces.Authentication
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.collections.mutableMapOf

class MockAuthentication @Inject constructor() : Authentication {

    private val users = mutableMapOf<String, String>("spas@gmail.com" to "123456")
    private val authStateFlow = MutableStateFlow(false)
    override suspend fun signup(
        email: String,
        password: String,
    ): AuthResponse {
        return if (users.containsKey(email)) {
            AuthResponse.Failure(Exception("User is already registered"))
        } else {
            authStateFlow.value = true
            users.put(email, password)
            AuthResponse.Success("testId")
        }
    }

    override suspend fun login(
        email: String,
        password: String,
    ): AuthResponse {
        return if (users.containsKey(email) && users[email] == password) {
            authStateFlow.value = true
            AuthResponse.Success("testId")
        } else {
            AuthResponse.Failure(Exception("Incorrect password or unregistered user"))
        }
    }

    override suspend fun logout() {
        authStateFlow.value = false
    }

    override suspend fun deleteAccount() { /* TODO() */
    }

    override suspend fun passwordResetByEmail(email: String): Task<Void> = Tasks.forResult(null)

    override suspend fun passwordResetForLoggedUser(): Task<Void> = Tasks.forResult(null)

    override fun authStateFlow(): Flow<Boolean> = authStateFlow

    override suspend fun initDataSources() {
        /* TODO("Not yet implemented") */
    }

    override suspend fun createDataSources(username: String, userId: String) {
        /* TODO("Not yet implemented") */
    }

    override suspend fun updatePassword(newPassword: String): Task<Void> = Tasks.forResult(null)

    override suspend fun reauthenticate(password: String): Task<Void> = Tasks.forResult(null)

    override suspend fun getEmail(): String = "test@email.com"
}