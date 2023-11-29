package bg.zahov.app.editProfile

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.mediators.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditProfileViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance()
    private val currUser = auth.currentUser
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName
    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    init {
        viewModelScope.launch {
            repo.getUsername(currUser!!.uid).let {
                _userName.postValue(it ?: "No username found")
            }
            _userEmail.postValue(currUser.email)
        }
    }

    fun changeUserName(newUserName: String){
        viewModelScope.launch {
            repo.changeUserName(auth.uid!!, newUserName)
        }
    }
    fun changeEmail(newEmail: String){
        auth.signInWithEmailAndPassword(auth.currentUser!!.email!! , auth.uid!!).addOnCompleteListener { task->
            if(task.isSuccessful){
                auth.currentUser!!.updateEmail(newEmail).addOnCompleteListener {
                    if(task.isSuccessful){
                      Log.d("YAY", "Email changed")
                    }else{

                    }
                }
            }

        }
    }
}