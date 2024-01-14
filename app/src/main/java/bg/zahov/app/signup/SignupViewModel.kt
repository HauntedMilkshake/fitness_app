package bg.zahov.app.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.backend.Settings
import bg.zahov.app.backend.User
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.utils.isAValidEmail
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var repo: UserRepository? = null
    fun signUp(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        if (areFieldsEmpty(userName, email, password)) {
            callback(false, "Cannot have empty fields")
            return
        }

        if (!email.isAValidEmail()) {
            callback(false, "Invalid email!")
            return
        }

        if (password != confirmPassword) {
            callback(false, "Passwords must match")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {

                        auth.currentUser?.uid?.let {
                            Log.d("SYNC SIGNUP BLOCK", it)
                            if (repo == null) {
                                repo = UserRepository.getInstance(it)
                            } else {
                                repo!!.updateUser(it)
                            }
                        }

                        repo?.createRealm(
                            User().apply {
                                username = userName
                                numberOfWorkouts = 0
                            },
                            workouts = null,
                            exercises = null,
                            settings = Settings()
                        )

                        repo?.createFirestore(
                            User().apply {
                                username = userName
                                numberOfWorkouts = 0
                            },
                            settings = Settings()
                        )
                        callback(true, null)
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    private fun areFieldsEmpty(userName: String?, email: String?, pass: String?) =
        listOf(userName, email, pass).count { it.isNullOrEmpty() } >= 1
}

