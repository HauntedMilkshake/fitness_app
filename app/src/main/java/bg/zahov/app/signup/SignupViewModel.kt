package bg.zahov.app.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.realm_db.User
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var repo: UserRepository
    fun signUp(
        userName: String,
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch() {
                        repo = UserRepository.getInstance(auth.currentUser!!.uid)

                        repo.createRealm(
                            User().apply {
                                username = userName
                                numberOfWorkouts = 0
                            },
                            workouts = null,
                            exercises = null,
                            settings = Settings()
                        )
                        viewModelScope.launch(Dispatchers.Default) {
                            repo.syncToFirestore(
                                User().apply {
                                    username = userName
                                    numberOfWorkouts = 0
                                },
                                workouts = null,
                                exercises = null,
                                settings = Settings()
                            )
                        }
                        callback(true, null)
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}

