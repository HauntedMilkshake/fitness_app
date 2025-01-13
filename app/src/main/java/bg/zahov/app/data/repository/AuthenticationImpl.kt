package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.remote.FirebaseAuthentication

class AuthenticationImpl : Authentication {
    companion object {
        @Volatile
        private var instance: AuthenticationImpl? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AuthenticationImpl().also { instance = it }
            }
    }

    private val auth = FirebaseAuthentication.getInstance()

    override suspend fun login(email: String, password: String) = auth.login(email, password)

    override suspend fun signup(email: String, password: String) =
        auth.signup(email, password)

    override suspend fun logout() = auth.logout()

    override suspend fun deleteAccount() = auth.deleteAccount()


    override suspend fun passwordResetByEmail(email: String) = auth.passwordResetByEmail(email)

    override suspend fun passwordResetForLoggedUser() = auth.passwordResetForLoggedUser()

    override fun authStateFlow() = auth.getAuthStateFlow()

    override suspend fun initDataSources() = auth.init()
    override suspend fun createDataSources(username: String, userId: String) = auth.create(username, userId)

    override suspend fun updatePassword(newPassword: String) = auth.updatePassword(newPassword)

    override suspend fun reauthenticate(password: String) = auth.reauthenticate(password)

    override suspend fun getEmail(): String = auth.getEmail()
}