package bg.zahov.app.data.interfaces

import kotlinx.coroutines.flow.Flow


interface Authentication {
    suspend fun signup(username: String, email: String, password: String)
    suspend fun login(email: String, password: String)
    suspend fun logout()
    suspend fun deleteAccount()
    suspend fun passwordResetByEmail(email: String): Boolean
    suspend fun passwordResetForLoggedUser(): Boolean
    fun isAuthenticated() : Boolean
    suspend fun initDataSources()
    suspend fun updatePassword(newPassword: String)
    suspend fun updateEmail(newEmail: String)
    suspend fun reauthenticate(password: String): Flow<Boolean>
}