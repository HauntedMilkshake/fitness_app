package bg.zahov.app.data.interfaces

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow


interface Authentication {
    suspend fun signup(email: String, password: String) : Task<AuthResult>
    suspend fun login(email: String, password: String) : Task<AuthResult>
    suspend fun logout()
    suspend fun deleteAccount()
    suspend fun passwordResetByEmail(email: String): Task<Void>
    suspend fun passwordResetForLoggedUser(): Task<Void>
    fun authStateFlow() : Flow<Boolean>
    suspend fun initDataSources()
    suspend fun createDataSources(username: String, userId: String)
    suspend fun updatePassword(newPassword: String): Task<Void>
    suspend fun reauthenticate(password: String): Task<Void>
    suspend fun getEmail(): String
}