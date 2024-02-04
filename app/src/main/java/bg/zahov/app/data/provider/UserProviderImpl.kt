package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.app.data.model.User
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.data.repository.UserRepositoryImpl
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

class UserProviderImpl: UserProvider {
    companion object {
        @Volatile
        private var instance: UserProviderImpl? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: UserProviderImpl().also { instance = it }
            }
    }

    private val userRepo = UserRepositoryImpl.getInstance()
    private val auth = AuthenticationImpl.getInstance()

    override suspend fun getUser(): Flow<User> = userRepo.getUser()

    override suspend fun changeUserName(newUsername: String) = userRepo.changeUserName(newUsername)

    override suspend fun signup(username: String, email: String, password: String,
    ): Task<AuthResult> = auth.signup(username, email, password)

    override suspend fun login(email: String, password: String): Task<AuthResult> = auth.login(email, password)

    override suspend fun logout() = auth.logout()

    override suspend fun deleteAccount() = auth.logout()
    override suspend fun passwordResetByEmail(email: String): Task<Void> = auth.passwordResetByEmail(email)

    override suspend fun passwordResetForLoggedUser(): Task<Void> = auth.passwordResetForLoggedUser()

    override fun isAuthenticated(): Boolean = auth.isAuthenticated()

    override suspend fun initDataSources() = auth.initDataSources()

    override suspend fun updatePassword(newPassword: String): Task<Void> = auth.updatePassword(newPassword)

    override suspend fun updateEmail(newEmail: String): Task<Void> = auth.updateEmail(newEmail)

    override suspend fun reauthenticate(password: String): Task<Void> = auth.reauthenticate(password)
    override suspend fun getEmail(): Flow<String> = auth.getEmail()
}