package bg.zahov.app.data.remote

import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.FirebaseAuthentication
import bg.zahov.app.data.interfaces.FirestoreManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class FirebaseAuthenticationImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirestoreManager,
) : FirebaseAuthentication {

    /**
     * Registers a new user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the sign-up process.
     */
    override suspend fun signup(email: String, password: String): AuthResult = withTimeout(5000) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    /**
     * Authenticates a user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the login process.
     */
    override suspend fun login(email: String, password: String): AuthResult = withTimeout(5000) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override fun logout() {
        auth.signOut()
    }

    override fun deleteAccount() {
        try {
            auth.currentUser?.delete()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthenticationException(e.message)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw AuthenticationException(e.message)
        }
    }

    override suspend fun passwordResetForLoggedUser(): Task<Void> = withContext(Dispatchers.IO) {
        auth.uid?.let { auth.sendPasswordResetEmail(it) } ?: Tasks.forResult(null)
    }

    override suspend fun passwordResetByEmail(email: String) = withContext(Dispatchers.IO) {
        auth.sendPasswordResetEmail(email)
    }

    override fun getAuthStateFlow(): Flow<Boolean> = callbackFlow {
        trySend(auth.currentUser != null)

        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.distinctUntilChanged()

    override fun initFirestoreUser() {
        auth.currentUser?.uid?.let {
            firestore.initUser(it)
        }
    }

    override suspend fun create(username: String, userId: String) {
        firestore.createFirestore(username, userId)
    }

    override suspend fun updatePassword(newPassword: String): Task<Void> =
        withContext(Dispatchers.IO) {
            auth.currentUser?.updatePassword(newPassword) ?: Tasks.forResult(null)
        }

    override suspend fun reauthenticate(password: String): Task<Void> =
        withContext(Dispatchers.IO) {
            auth.currentUser?.email?.let {
                auth.currentUser?.reauthenticate(EmailAuthProvider.getCredential(it, password))
            } ?: Tasks.forResult(null)
        }

    override suspend fun getEmail() = withContext(Dispatchers.IO) {
        auth.currentUser?.email ?: throw CriticalDataNullException("NO EMAIL FOUND")
    }
}

