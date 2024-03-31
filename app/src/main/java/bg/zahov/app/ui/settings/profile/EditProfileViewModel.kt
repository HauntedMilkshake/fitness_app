package bg.zahov.app.ui.settings.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.getUserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getUserProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _isUnlocked = MutableLiveData(false)
    val isUnlocked: LiveData<Boolean>
        get() = _isUnlocked

//    private var userPassword: String? = null

    init {
        viewModelScope.launch {
            try {
                repo.getUser().collect {
                    _name.postValue(it.name)
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(e.message, true))
            }

            try {
                _email.postValue(repo.getEmail())
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(e.message, true))
            }
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername.isEmpty()) {
            _state.value = State.Error("Username cannot be empty", false)
            return
        }

        viewModelScope.launch {

            repo.changeUserName(newUsername)
                .addOnSuccessListener {
                    _name.postValue(newUsername)
                    _state.value = State.Notify("Successfully updated username")
                }
                .addOnFailureListener {
                    _state.value = State.Error("Successfully updated username", false)
                }

        }
    }

    fun updateEmail(newEmail: String) {
        if (!newEmail.isEmail()) {
            _state.value = State.Notify("Email is not valid!")
            return
        }
        viewModelScope.launch {
            repo.updateEmail(newEmail).addOnSuccessListener {
                _state.postValue(State.Notify("Successfully updated email"))
            }
                .addOnFailureListener {
                    _state.postValue(State.Error("Couldn't update email", false))
                }
        }
    }

    fun unlockFields(password: String) {
        Log.d("REAUTH", "REATUH")
        if (password.isEmpty() || password.length < 6) {
            _state.value = State.Error("Incorrect password!", false)
            return
        }
        Log.d("correct password", "yohoo")
        viewModelScope.launch {
            Log.d("opening vm scope", "vm scope ")
            repo.reauthenticate(password)
                .addOnSuccessListener {
                    Log.d("authenticated succesfully", "yay")
                    _state.postValue(State.Notify("Successfully re-authenticated!"))
                    _isUnlocked.postValue(true)
                }
        }
    }

    fun sendPasswordResetLink() {
        viewModelScope.launch {

            repo.passwordResetForLoggedUser()
                .addOnSuccessListener {
                    _state.postValue(State.Notify("Successfully updated password"))
                }
                .addOnFailureListener {
                    _state.postValue(State.Error("Failed to send password reset link", false))
                }
        }
    }

    fun updatePassword(newPassword: String) {
        if (newPassword.isEmpty() || newPassword.length < 6) {
            _state.value = State.Error("Password must be at least 6 characters long", false)
            return
        }

        viewModelScope.launch {
            repo.updatePassword(newPassword)
                .addOnSuccessListener {
                    _state.postValue(State.Notify("Successfully updated password"))
                }
                .addOnFailureListener {
                    _state.postValue(State.Error("Failed to update password", false))
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("activitiy" ,viewModelScope.isActive.toString())

        Log.d("on cleard", "on cleared")
    }
    sealed interface State {
        data class Error(val error: String?, val shutdown: Boolean) : State
        data class Notify(val message: String) : State
    }
}
