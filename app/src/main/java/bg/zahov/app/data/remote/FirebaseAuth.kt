package bg.zahov.app.data.remote

import android.util.Log
import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.exception.DeleteRealmException
import bg.zahov.app.data.local.RealmManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

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

    suspend fun signup(username: String, email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password).also {
            if (it.isSuccessful) {
                init()
                firestore.createFirestore(username)
                realm.createRealm()
            }
        }

    suspend fun login(email: String, password: String) = auth.signInWithEmailAndPassword(email, password).also {
            Log.d("SEX", "INVOCATION")
            init()
            realm.createRealm()
        }

//    suspend fun onLoginSuccess() {
//        init()
//        realm.createRealm()
//    }

    suspend fun logout() {
        resetResources()
        auth.signOut()
    }

    suspend fun deleteAccount() {
        try {
            resetResources()
        } catch (e: IllegalStateException) {
            throw DeleteRealmException(e.message)
        }

        try {
            auth.currentUser?.delete()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthenticationException(e.message)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw AuthenticationException(e.message)
        }
    }

    private suspend fun resetResources() {
        realm.deleteRealm()
    }

    suspend fun passwordResetForLoggedUser(): Task<Void> = withContext(Dispatchers.IO) {
        auth.uid?.let { auth.sendPasswordResetEmail(it) } ?: Tasks.forResult(null)
    }

    suspend fun passwordResetByEmail(email: String) = withContext(Dispatchers.IO) {
        auth.sendPasswordResetEmail(email)
    }

    fun isAuthenticated() = auth.currentUser != null

    fun init() {
        Log.d("init", "${auth.currentUser?.uid}")
        auth.currentUser?.uid?.let {
            firestore.initUser(it)
        }
    }

    suspend fun updatePassword(newPassword: String): Task<Void> = withContext(Dispatchers.IO) {
        auth.currentUser?.updatePassword(newPassword) ?: Tasks.forResult(null)
    }

    suspend fun updateEmail(newEmail: String): Task<Void> = withContext(Dispatchers.IO) {
        try {
            auth.currentUser?.let { updateEmail(newEmail) } ?: Tasks.forResult(null)
        } catch (e: FirebaseAuthException) {
            throw AuthenticationException(e.message ?: "tough luck")
        }
    }

    suspend fun reauthenticate(password: String): Task<Void> = withContext(Dispatchers.IO) {
        auth.currentUser?.email?.let {
            auth.currentUser?.reauthenticate(EmailAuthProvider.getCredential(it, password))
        } ?: Tasks.forResult(null)
    }
    suspend fun getEmail() = withContext(Dispatchers.IO) {
        flow<String> {
            auth.currentUser?.email ?: throw CriticalDataNullException("NO EMAIL FOUND")
        }
    }
}

