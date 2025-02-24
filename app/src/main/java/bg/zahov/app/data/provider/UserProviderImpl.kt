package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.model.User
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProviderImpl @Inject constructor(
    private val userRepo: UserRepository,
    private val auth: Authentication,
) : UserProvider {

    override suspend fun getUser(): Flow<User> = userRepo.getUser()

    override suspend fun changeUserName(newUsername: String) = userRepo.changeUserName(newUsername)

    override suspend fun signup(email: String, password: String) = auth.signup(email, password)

    override suspend fun login(email: String, password: String) = auth.login(email, password)

    override suspend fun logout() = auth.logout()

    override suspend fun deleteAccount() = auth.logout()
    override suspend fun passwordResetByEmail(email: String): Task<Void> =
        auth.passwordResetByEmail(email)

    override suspend fun passwordResetForLoggedUser(): Task<Void> =
        auth.passwordResetForLoggedUser()

    override fun authStateFlow(): Flow<Boolean> = auth.authStateFlow()

    override suspend fun initDataSources() = auth.initDataSources()
    override suspend fun createDataSources(username: String, userId: String) =
        auth.createDataSources(username, userId)

    override suspend fun updatePassword(newPassword: String): Task<Void> =
        auth.updatePassword(newPassword)


    override suspend fun reauthenticate(password: String): Task<Void> =
        auth.reauthenticate(password)

    override suspend fun getEmail(): String = auth.getEmail()
}
