package bg.zahov.app.editProfile

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName
    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail
    val isUnlocked: LiveData<Boolean> get() = _isUnlocked
    private val _isUnlocked = MutableLiveData(false)

    init {
        viewModelScope.launch {

            _userEmail.postValue(auth.currentUser?.email)

            repo.getUser()!!.collect {
                when (it) {
                    is InitialObject -> {
                        _userName.postValue(it.obj.username)
                    }

                    is UpdatedObject -> {
                        _userName.postValue(it.obj.username)
                    }

                    is DeletedObject -> {}
                }
            }
        }
    }

    fun updateUsername(newUsername: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if(newUsername != _userName.value && newUsername.isNotEmpty()){
                repo.changeUserName(newUsername)
                callback(true, "Successfully updated username!")
            }else{
                callback(false, "Couldn't update username!")
            }
        }
    }

    fun updateEmail(newEmail: String, callback: (Boolean, String) -> Unit) {
        if(newEmail != auth.currentUser!!.email && isEmailValid(newEmail) && newEmail.isNotEmpty()){
            auth.currentUser!!.updateEmail(newEmail)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        callback(true, "Successfully updated email!")
                    }else{
                        callback(false, "Couldn't update email!")
                    }
                }
        }
    }
    fun unlockFields(password: String, callback: (Boolean) -> Unit){
        auth.currentUser!!.reauthenticate(EmailAuthProvider.getCredential(auth.currentUser?.email!!, password))
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d("CHECK", "SUCCESSFULLY LOGGED IN")
                    _isUnlocked.value = true
                    callback(true)
                }else{
                    callback(false)
                }
            }
    }
    fun sendPasswordResetLink(callback: (Boolean, String) -> Unit){
        auth.sendPasswordResetEmail(auth.currentUser!!.email!!).addOnCompleteListener {task ->
            if(task.isSuccessful){
                callback(true, "Password link sent!")
            }else{
                callback(false, "Couldn't send password link!")
            }
        }

    }
    fun updatePassword(newPassword: String, callback: (Boolean, String) -> Unit){
        if(newPassword.isNotEmpty()){
            auth.currentUser!!.updatePassword(newPassword)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        callback(true, "Successfully updated password!")
                    }else{
                        callback(false, "Couldn't update password!")
                    }
                }
        }
    }
    private fun isEmailValid(email: String) = Regex("^\\S+@\\S+\\.\\S+$").matches(email)

}
