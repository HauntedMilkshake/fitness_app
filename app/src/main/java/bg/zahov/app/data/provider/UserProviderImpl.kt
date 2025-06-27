package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.model.User
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProviderImpl @Inject constructor(
    private val userRepo: UserRepository,
    private val auth: Authentication,
) {

    suspend fun getUser(): Flow<User> = userRepo.getUser()

    suspend fun changeUserName(newUsername: String) = userRepo.changeUserName(newUsername)

    suspend fun signup(email: String, password: String) = auth.signup(email, password)

    suspend fun login(email: String, password: String) = auth.login(email, password)

    suspend fun logout() = auth.logout()

    suspend fun deleteAccount() = auth.logout()
    suspend fun passwordResetByEmail(email: String): Task<Void> =
        auth.passwordResetByEmail(email)

    suspend fun passwordResetForLoggedUser(): Task<Void> =
        auth.passwordResetForLoggedUser()

    fun authStateFlow(): Flow<Boolean> = auth.authStateFlow()

    suspend fun initDataSources() = auth.initDataSources()
    suspend fun createDataSources(username: String, userId: String) =
        auth.createDataSources(username, userId)

    suspend fun updatePassword(newPassword: String): Task<Void> =
        auth.updatePassword(newPassword)


    suspend fun reauthenticate(password: String): Task<Void> =
        auth.reauthenticate(password)

    suspend fun getEmail(): String = auth.getEmail()
}
