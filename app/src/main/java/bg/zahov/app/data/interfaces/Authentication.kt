package bg.zahov.app.data.interfaces

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow


interface Authentication {
    suspend fun signup(email: String, password: String): AuthResponse
    suspend fun login(email: String, password: String): AuthResponse
    suspend fun logout()
    suspend fun deleteAccount()
    suspend fun passwordResetByEmail(email: String): Task<Void>
    suspend fun passwordResetForLoggedUser(): Task<Void>
    fun authStateFlow(): Flow<Boolean>
    suspend fun initDataSources()
    suspend fun createDataSources(username: String, userId: String)
    suspend fun updatePassword(newPassword: String): Task<Void>
    suspend fun reauthenticate(password: String): Task<Void>
    suspend fun getEmail(): String
}

sealed interface AuthResponse {
    data class Success(val userId: String) : AuthResponse
    data class Failure(val error: Throwable) : AuthResponse
}