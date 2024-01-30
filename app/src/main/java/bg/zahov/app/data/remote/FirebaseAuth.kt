package bg.zahov.app.data.remote

import android.util.Log
import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.local.RealmManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private val realm = RealmManager.getInstance()

    suspend fun signup(username: String, email: String, password: String) {
        val signupResult = auth.createUserWithEmailAndPassword(email, password).await()
//            .addOnFailureListener { throw AuthenticationException("TODO(Handle later)") }

        signupResult.user?.uid?.let {
            init()
            CoroutineScope(Dispatchers.IO).launch {
                firestore.createFirestore(username)
                realm.createRealm()
            }
        }
    }

    suspend fun login(email: String, password: String) {
        Log.d("login", "calling from source")
        val loginResult = auth.signInWithEmailAndPassword(email, password).await()
        Log.d("login", "success")

        loginResult.user?.uid?.let {
            Log.d("login", it)
            init()
            CoroutineScope(Dispatchers.IO).launch {
                realm.createRealm()
            }
        }
    }

    suspend fun logout() {
        resetResources()
        auth.signOut()
    }

    suspend fun deleteAccount() {
        resetResources()
        try {
            auth.currentUser?.delete()
        } catch (e : FirebaseAuthInvalidUserException) {
            throw e
        }
    }

    private suspend fun resetResources() {
        firestore.resetUser()
        realm.deleteRealm()
    }
    suspend fun passwordResetByEmail(email: String): Boolean = suspendCoroutine {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                it.resume(task.isSuccessful)
            }
    }

    suspend fun passwordResetForLoggedUser(): Boolean = suspendCoroutine {
        auth.currentUser?.email?.let { id ->
            auth.sendPasswordResetEmail(id)
                .addOnCompleteListener { task ->
                    it.resume(task.isSuccessful)
                }
        }
    }

    fun isAuthenticated() = auth.currentUser != null

    fun init() {
        Log.d("init", "${auth.currentUser?.uid}")
        auth.currentUser?.uid?.let {
            firestore.initUser(it)
        }
    }

    suspend fun updatePassword(newPassword: String) {
        auth.currentUser?.updatePassword(newPassword)
    }

    suspend fun updateEmail(newEmail: String) {
        try {
            auth.currentUser?.updateEmail(newEmail)
        } catch (e: FirebaseAuthException) {
            throw AuthenticationException(e.message ?: "tough luck")
        }
    }

    suspend fun reauthenticate(password: String) = callbackFlow {
        auth.currentUser?.email?.let {
            auth.currentUser?.reauthenticate(EmailAuthProvider.getCredential(it, password))
                ?.addOnCompleteListener {
                    trySend(true)
                } ?: trySend(false)
        }
        awaitClose { close() }
    }
}