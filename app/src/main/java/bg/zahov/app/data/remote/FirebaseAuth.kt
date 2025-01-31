package bg.zahov.app.data.remote

import android.util.Log
import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.exception.CriticalDataNullException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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

class FirebaseAuthentication {
    companion object {
        @Volatile
        private var instance: FirebaseAuthentication? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirebaseAuthentication().also { instance = it }
            }
    }

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirestoreManager.getInstance()

    suspend fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        init()
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
        init()
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


    private fun init() {
        auth.currentUser?.uid?.let {
            Log.d("init", "init")
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

