package bg.zahov.app.data.remote

import android.util.Log
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

class FirebaseAuthentication {
    companion object {
        private var instance: FirebaseAuthentication? = null
        fun getInstance() = instance ?: FirebaseAuthentication().also { instance = it }

    }

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirestoreManager.getInstance()

    /**
     * Registers a new user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the sign-up process.
     */
    suspend fun signup(email: String, password: String): AuthResult = withTimeout(5000) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }


    /**
     * Authenticates a user with the given email and password.
     * @param email User's email.
     * @param password User's password.
     * @return [AuthResult] of the login process.
     */
    suspend fun login(email: String, password: String): AuthResult = withTimeout(5000) {
        auth.signInWithEmailAndPassword(email, password).await()
    }


    fun logout() {
        auth.signOut()
    }

    fun deleteAccount() {
        try {
            auth.currentUser?.delete()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthenticationException(e.message)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw AuthenticationException(e.message)
        }
    }

    suspend fun passwordResetForLoggedUser(): Task<Void> = withContext(Dispatchers.IO) {
        auth.uid?.let { auth.sendPasswordResetEmail(it) } ?: Tasks.forResult(null)
    }

    suspend fun passwordResetByEmail(email: String) = withContext(Dispatchers.IO) {
        auth.sendPasswordResetEmail(email)
    }

    fun getAuthStateFlow(): Flow<Boolean> = callbackFlow {
        trySend(auth.currentUser != null)

        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.distinctUntilChanged()

    fun initFirestoreUser() {
        auth.currentUser?.uid?.let {
            firestore.initUser(it)
        }
    }

    suspend fun create(username: String, userId: String) {
        firestore.createFirestore(username, userId)
    }

    suspend fun updatePassword(newPassword: String): Task<Void> = withContext(Dispatchers.IO) {
        auth.currentUser?.updatePassword(newPassword) ?: Tasks.forResult(null)
    }

    suspend fun reauthenticate(password: String): Task<Void> = withContext(Dispatchers.IO) {
        auth.currentUser?.email?.let {
            auth.currentUser?.reauthenticate(EmailAuthProvider.getCredential(it, password))
        } ?: Tasks.forResult(null)
    }

    suspend fun getEmail() = withContext(Dispatchers.IO) {
        auth.currentUser?.email ?: throw CriticalDataNullException("NO EMAIL FOUND")
    }
}

