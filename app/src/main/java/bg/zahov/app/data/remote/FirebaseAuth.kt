package bg.zahov.app.data.remote

import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.exception.CriticalDataNullException
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

open class FirebaseAuthentication @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirestoreManager,
) {

    /**
     * Registers a new user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the sign-up process.
     */
     open suspend fun signup(email: String, password: String): AuthResult = withTimeout(5000) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    /**
     * Authenticates a user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the login process.
     */
     open suspend fun login(email: String, password: String): AuthResult = withTimeout(5000) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

     open fun logout() {
        auth.signOut()
    }

     open fun deleteAccount() {
        try {
            auth.currentUser?.delete()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthenticationException(e.message)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw AuthenticationException(e.message)
        }
    }

     open suspend fun passwordResetForLoggedUser(): Task<Void> = withContext(Dispatchers.IO) {
        auth.uid?.let { auth.sendPasswordResetEmail(it) } ?: Tasks.forResult(null)
    }

     open suspend fun passwordResetByEmail(email: String) = withContext(Dispatchers.IO) {
        auth.sendPasswordResetEmail(email)
    }

     open fun getAuthStateFlow(): Flow<Boolean> = callbackFlow {
        trySend(auth.currentUser != null)

        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.distinctUntilChanged()

     open fun initFirestoreUser() {
        auth.currentUser?.uid?.let {
            firestore.initUser(it)
        }
    }

     open suspend fun create(username: String, userId: String) {
        firestore.createFirestore(username, userId)
    }

     open suspend fun updatePassword(newPassword: String): Task<Void> =
        withContext(Dispatchers.IO) {
            auth.currentUser?.updatePassword(newPassword) ?: Tasks.forResult(null)
        }

     open suspend fun reauthenticate(password: String): Task<Void> =
        withContext(Dispatchers.IO) {
            auth.currentUser?.email?.let {
                auth.currentUser?.reauthenticate(EmailAuthProvider.getCredential(it, password))
            } ?: Tasks.forResult(null)
        }

     open suspend fun getEmail() = withContext(Dispatchers.IO) {
        auth.currentUser?.email ?: throw CriticalDataNullException("NO EMAIL FOUND")
    }
}

