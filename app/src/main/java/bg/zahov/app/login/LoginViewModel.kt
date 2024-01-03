package bg.zahov.app.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var repo: UserRepository
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if(emailIsValid(email) && email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        repo = UserRepository.getInstance(auth.currentUser!!.uid)

                        runBlocking {
                            repo.syncFromFirestore()
                        }

                        callback(true, null)
                    } else {
                        callback(false, task.exception?.message)
                    }
                }
        }else{
            callback(false, "Please ensure your fields aren't empty and are valid")
        }

    }

    fun sendPasswordResetEmail(email: String, callback: (String) -> Unit) {
        if(emailIsValid(email) && email.isNotEmpty()){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                        callback(if(task.isSuccessful) "Password link sent!" else task.exception?.message ?: "Failed to send email reset link")
                }
        }else{
            callback("Please ensure you have a valid email!")
        }

    }
    private fun emailIsValid(email: String) = Regex("^\\S+@\\S+\\.\\S+$").matches(email)
}
