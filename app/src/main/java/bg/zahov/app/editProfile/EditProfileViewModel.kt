package bg.zahov.app.editProfile

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName
    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    init {
        viewModelScope.launch {
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

    fun changeUserName(newUserName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.changeUserName(newUserName)
        }
    }

    fun changeEmail(newEmail: String) {
        auth.signInWithEmailAndPassword(auth.currentUser!!.email!!, auth.uid!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser!!.updateEmail(newEmail).addOnCompleteListener {
                        if (task.isSuccessful) {
                            Toast.makeText(
                                getApplication(),
                                "Successfully changed email!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                getApplication(),
                                "Successfully changed email!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }
}