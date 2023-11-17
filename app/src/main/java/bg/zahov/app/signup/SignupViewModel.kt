package bg.zahov.app.signup

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bg.zahov.app.realm_db.RealmManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableLiveData<Boolean>()
    private val database = FirebaseDatabase.getInstance().reference
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated
    init{
        _isAuthenticated.value = false
    }
    fun signUp(userName: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                    _isAuthenticated.postValue(true)
                    RealmManager.RealmProcessor.createRealm(userName)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}

