package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.AuthResponse
import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.remote.FirebaseAuthentication
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class AuthenticationImpl : Authentication {
    companion object {
        private var instance: AuthenticationImpl? = null
        fun getInstance() = instance ?: AuthenticationImpl().also { instance = it }
    }

    private val auth = FirebaseAuthentication.getInstance()

    /**
     * Authenticates a user with the given email and password.
     * Maps the result to a standardized AuthResponse format.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResponse] indicating success or failure.
     *
     * @see [AuthResponseMapper]
     */
    override suspend fun login(email: String, password: String): AuthResponse =
        AuthResponseMapper.map(auth.login(email, password))
            .also { (it as? AuthResponse.Success)?.let { uid -> auth.initFirestoreUser() } }

    /**
     * Registers a new user with the given email and password.
     * Maps the result to a standardized AuthResponse format.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResponse] indicating success or failure.
     *
     *@see [AuthResponseMapper]
     */
    override suspend fun signup(email: String, password: String): AuthResponse =
        AuthResponseMapper.map(auth.signup(email, password))
            .also { (it as? AuthResponse.Success)?.let { uid -> auth.initFirestoreUser() } }

    override suspend fun logout() = auth.logout()

    override suspend fun deleteAccount() = auth.deleteAccount()

    override suspend fun passwordResetByEmail(email: String) = auth.passwordResetByEmail(email)

    override suspend fun passwordResetForLoggedUser() = auth.passwordResetForLoggedUser()

    override fun authStateFlow() = auth.getAuthStateFlow()

    override suspend fun initDataSources() {
        auth.initFirestoreUser()
    }

    override suspend fun createDataSources(username: String, userId: String) =
        auth.create(username, userId)

    override suspend fun updatePassword(newPassword: String) = auth.updatePassword(newPassword)

    override suspend fun reauthenticate(password: String) = auth.reauthenticate(password)

    override suspend fun getEmail(): String = auth.getEmail()
}

/**
 * Maps authentication results from different sources to a standardized AuthResponse format.
 */
object AuthResponseMapper {

    /**
     * Converts the given authentication result into an AuthResponse.
     * @param result The authentication result, which could be from Firebase or another service.
     * @return [AuthResponse.Success] or [AuthResponse.Failure] respectfully
     */
    fun <T> map(result: T): AuthResponse = when (result) {
        is AuthResult -> {
            if (result.user?.uid == null) {
                AuthResponse.Failure(Exception("Unable to authenticate"))
            } else {
                AuthResponse.Success(userId = result.user?.uid ?: "")
            }

        }
        else -> {
            AuthResponse.Failure(Exception("Unable to authenticate"))
        }
    }
}