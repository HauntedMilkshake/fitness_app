package bg.zahov.app.ui.settings.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.MyApplication
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.getUserProvider
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

    private var userPassword: String? = null

    //
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
        viewModelScope.launch {
            //TODO()
            //auth.updateEmail(newEmail)
        }
    }

    fun unlockFields(password: String) {
        if (password.isEmpty() || password.length < 6) {
            _state.value = State.Error("Incorrect password!", false)
            return
        }

        viewModelScope.launch {

            repo.reauthenticate(password)
                .addOnSuccessListener {
                    _state.postValue(State.Notify("Successfully reauthenticated!"))
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
        if (newPassword.isEmpty() && newPassword.length < 6) {
            _state.value = State.Error("Password must be atleast 6 characters long", false)
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

    sealed interface State {
        data class Error(val error: String?, val shutdown: Boolean) : State
        data class Notify(val message: String) : State
    }
}
