package bg.zahov.app.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    fun signUp(
        userName: String,
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        database.collection("users")
                            .document(auth.currentUser!!.uid)
                            .set(hashMapOf("username" to userName))
                            .addOnSuccessListener {
                                callback(true, null)
                                //this may be bad ¯\_(ツ)_/¯
                                viewModelScope.launch {
//                                    RealmManager.getInstance().createRealm(userId = auth.currentUser!!.uid, uName = userName)
                                }
                            }
                            .addOnFailureListener {
                                callback(false, it.message)
                            }
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}
//TODO(REALM)

