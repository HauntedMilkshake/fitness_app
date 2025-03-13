package bg.zahov.app.data.interfaces

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthentication {

    /**
     * Registers a new user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the sign-up process.
     */
    suspend fun signup(email: String, password: String): AuthResult

    /**
     * Authenticates a user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the login process.
     */
    suspend fun login(email: String, password: String): AuthResult

    /**
     * Logs out the current authenticated user.
     */
    fun logout()

    /**
     * Deletes the current authenticated user's account.
     */
    fun deleteAccount()

    /**
     * Resets the password for the currently logged-in user.
     * @return Task that indicates the result of the password reset request.
     */
    suspend fun passwordResetForLoggedUser(): Task<Void>

    /**
     * Sends a password reset email to the specified email address.
     * @param email The email to send the reset link to.
     */
    suspend fun passwordResetByEmail(email: String): Task<Void>

    /**
     * Observes the authentication state of the current user.
     * @return A flow of boolean values indicating whether a user is authenticated or not.
     */
    fun getAuthStateFlow(): Flow<Boolean>

    /**
     * Initializes Firestore with the current user's UID.
     */
    fun initFirestoreUser()

    /**
     * Creates a new user in Firestore with the given username and userId.
     * @param username The username of the new user.
     * @param userId The user ID of the new user.
     */
    suspend fun create(username: String, userId: String)

    /**
     * Updates the password for the current user.
     * @param newPassword The new password.
     * @return Task that indicates the result of the password update request.
     */
    suspend fun updatePassword(newPassword: String): Task<Void>

    /**
     * Reauthenticates the current user with the provided password.
     * @param password The user's password.
     * @return Task that indicates the result of the reauthentication request.
     */
    suspend fun reauthenticate(password: String): Task<Void>

    /**
     * Retrieves the email of the currently authenticated user.
     * @return The email address of the authenticated user.
     * @throws CriticalDataNullException if the email is not found.
     */
    suspend fun getEmail(): String
}
