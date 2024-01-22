package bg.zahov.app.data.remote

import android.util.Log
import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.local.RealmManager
import bg.zahov.app.data.local.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationImpl : Authentication {
    companion object {
        @Volatile
        private var instance: AuthenticationImpl? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AuthenticationImpl().also { instance = it }
            }
    }

    private val auth = FirebaseAuth.getInstance()
    private val userId by lazy {
        auth.currentUser?.uid
    }

    override suspend fun login(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userId?.let {
                            //Might need to do something else with realm
                            FirestoreManager.getInstance().initUser(it)
                        }
                    } else throw AuthenticationException("Something went wrong try again")
                }
        } catch (e: Exception) {
            throw AuthenticationException("Error when logging in")
        }
    }

    override suspend fun signup(username: String, email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.Default).launch {
                            task.result.user?.uid?.let {
                                try {
                                    Log.d("SIGNUP IMPL", it)
                                    FirestoreManager.getInstance().initUser(it)
                                    FirestoreManager.getInstance().createFirestore(username)
                                    RealmManager.getInstance().createRealm(settings = Settings())
                                } catch (e: FirebaseFirestoreException) {
                                    throw AuthenticationException(
                                        e.message ?: "Error creating account"
                                    )
                                }
                            } ?: Log.d("SIGNUP IMPL", "no id")
                        }

                    } else throw AuthenticationException("Please restart the app and try again later")
                }

        } catch (e: FirebaseAuthException) {
            throw AuthenticationException("Please restart the app and try again later")
        }
    }

    override suspend fun logout() {
    }

    override suspend fun deleteAccount() {
    }

    override suspend fun passwordReset(email: String) {
        try {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        throw AuthenticationException("Failed sending you an email")
                    }
                }

        } catch (e: FirebaseAuthException) {
            throw AuthenticationException("Failed sending you an email")
        }
    }
}