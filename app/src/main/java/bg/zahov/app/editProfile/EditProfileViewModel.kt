package bg.zahov.app.editProfile

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application): AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val currUser = auth.currentUser
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName
    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    init {
        viewModelScope.launch {
            repo.getUsername().let {
                _userName.postValue(it)
            }
            _userEmail.postValue(currUser?.email ?: "no email")
        }
    }
    fun changeUserName(newUserName: String){
        viewModelScope.launch {
            repo.changeUserName(newUserName)
        }
    }
    //another option is to make it return a boolean and show the toast in the fragment
    fun changeEmail(newEmail: String){
        auth.signInWithEmailAndPassword(auth.currentUser!!.email!! , auth.uid!!).addOnCompleteListener { task->
            if(task.isSuccessful){
                auth.currentUser!!.updateEmail(newEmail).addOnCompleteListener {
                    if(task.isSuccessful){
                        Toast.makeText(getApplication(), "Successfully changed email!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(getApplication(), "Successfully changed email!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}