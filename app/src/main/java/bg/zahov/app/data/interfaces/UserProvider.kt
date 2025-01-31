package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.User
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

interface UserProvider {
    suspend fun getUser(): Flow<User>
    suspend fun changeUserName(newUsername: String): Task<Void>
    suspend fun signup(email: String, password: String)
    suspend fun login(email: String, password: String)
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