package bg.zahov.app.ui.settings.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.repository.UserRepositoryImpl
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.launch

//FIXME see comments in AuthViewModel
class EditProfileViewModel(application: Application) : AndroidViewModel(application){
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val repo = UserRepositoryImpl.getInstance(auth.currentUser!!.uid)
//
//    private val _userName = MutableLiveData<String>()
//    val userName: LiveData<String> get() = _userName
//    private val _userEmail = MutableLiveData<String>()
//    val userEmail: LiveData<String> get() = _userEmail
//    val isUnlocked: LiveData<Boolean> get() = _isUnlocked
//    private val _isUnlocked = MutableLiveData(false)
//
//    private var userPassword: String? = null
//
//    init {
//        viewModelScope.launch {
//            //FIXME you should have a single source of truth for the user in your VMs and
//            // that is the user object served by UserRepository (probably as a flow)
//            _userEmail.postValue(auth.currentUser?.email)
//            repo.getUser()!!.collect {
//                when (it) {
//                    is InitialObject -> {
//                        _userName.postValue(it.obj.username)
//                    }
//
//                    is UpdatedObject -> {
//                        _userName.postValue(it.obj.username)
//                    }
//
//                    is DeletedObject -> {
//                        _userName.postValue("")
//                    }
//                }
//            }
//        }
//    }
//
//    //FIXME  please don't use callbacks with Kotlin and coroutines
//    fun updateUsername(newUsername: String, callback: (String) -> Unit) {
//        viewModelScope.launch {
//            if (newUsername != _userName.value && newUsername.isNotEmpty()) {
//                repo.changeUserName(newUsername)
//                callback("Successfully updated username!")
//            } else if (newUsername != _userName.value) {
//                callback("Couldn't update username!")
//            }
//        }
//    }
//
//    //FIXME please don't use callbacks with Kotlin and coroutines
//    // Usage of !! operator is highly discouraged
//    // Check API deprecation and use the suggested substitute calls
//    //updateEmail deprecated ??
//    fun updateEmail(newEmail: String, callback: (String) -> Unit) {
//        if (newEmail != auth.currentUser!!.email && newEmail.isAValidEmail() && newEmail.isNotEmpty()) {
////            auth.currentUser!!.reauthenticate(EmailAuthProvider.getCredential(auth.currentUser!!.email!!, userPassword!!)).addOnCompleteListener {rTask ->
//            auth.signInWithEmailAndPassword(auth.currentUser!!.email!!, userPassword!!)
//                .addOnCompleteListener { rTask ->
//                    if (rTask.isSuccessful) {
//                        auth.currentUser!!.updateEmail(newEmail).addOnCompleteListener { eTask ->
//                            callback(if (eTask.isSuccessful) "Successfully updated email!" else "Couldn't update email!")
//                        }
//                    }
//                }
//        }
//    }
//
//    //FIXME please don't use callbacks with Kotlin and coroutines
//    fun unlockFields(password: String, callback: (Boolean, String) -> Unit) {
//        auth.currentUser!!.reauthenticate(
//            EmailAuthProvider.getCredential(
//                auth.currentUser?.email!!,
//                password
//            )
//        )
//            .addOnCompleteListener { task ->
//                _isUnlocked.value = task.isSuccessful
//                userPassword = password
//                callback(
//                    task.isSuccessful,
//                    if (task.isSuccessful) "Successfully logged in" else "Incorrect password"
//                )
//            }
//    }
//
//    //FIXME please don't use callbacks with Kotlin and coroutines
//    fun sendPasswordResetLink(callback: (String) -> Unit) {
//        auth.sendPasswordResetEmail(auth.currentUser!!.email!!).addOnCompleteListener { task ->
//            callback(if (task.isSuccessful) "Password link sent!" else "Couldn't send password link!")
//        }
//    }
//
//    //FIXME I see this is a pattern in the project - please don't use callbacks with Kotlin and coroutines
//    fun updatePassword(newPassword: String, callback: (String) -> Unit) {
//        if (newPassword.isNotEmpty()) {
//            auth.currentUser!!.updatePassword(newPassword).addOnCompleteListener { task ->
//                callback(if (task.isSuccessful) "Successfully updated password!" else "Couldn't update password!")
//            }
//        }
//    }
}
