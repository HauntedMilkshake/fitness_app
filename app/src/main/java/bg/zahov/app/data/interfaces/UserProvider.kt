package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface UserProvider {
    suspend fun getUser(): Flow<User>
    suspend fun changeUserName(newUsername: String): Task<Void>
    suspend fun signup(email: String, password: String): Task<AuthResult>
    suspend fun login(email: String, password: String): Task<AuthResult>
    suspend fun logout()
    suspend fun deleteAccount()
    suspend fun passwordResetByEmail(email: String): Task<Void>
    suspend fun passwordResetForLoggedUser(): Task<Void>
    fun isAuthenticated(): Boolean
    suspend fun initDataSources()
    suspend fun createDataSources(username: String)
    suspend fun updatePassword(newPassword: String): Task<Void>
    suspend fun updateEmail(newEmail: String): Task<Void>
    suspend fun reauthenticate(password: String): Task<Void>
    suspend fun getEmail(): String
}