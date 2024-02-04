package bg.zahov.app.ui.settings.profile

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.MyApplication
import bg.zahov.app.data.exception.CriticalDataNullException
import kotlinx.coroutines.launch

class EditProfileViewModel(application: MyApplication) : AndroidViewModel(application) {
    private val repo by lazy {
        application.userProvider
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
                repo.getEmail().collect() {
                    _email.postValue(it)
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(e.message, true))
            }
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername.isEmpty()) {
            _state.postValue(State.Error("Username cannot be empty", false))
            return
        }

        viewModelScope.launch {

            val result = repo.changeUserName(newUsername)

            if (result.isSuccessful) {
                _name.postValue(newUsername)
                _state.postValue(State.Error("Successfully updated username", false))
            } else {
                _state.postValue(
                    State.Error(
                        result.exception?.message ?: "Couldn't update username", false
                    )
                )
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
            _state.postValue(State.Notify("Incorrect password!"))
            return
        }

        viewModelScope.launch {

            val result = repo.reauthenticate(password)

            if (result.isSuccessful) {
                _state.postValue(State.Notify("Successfully reauthenticated!"))
                _isUnlocked.postValue(true)
            }

        }
    }

    fun sendPasswordResetLink() {
        viewModelScope.launch {

            val result = repo.passwordResetForLoggedUser()

            if (result.isSuccessful) {
                _state.postValue(State.Notify("Successfully updated password"))
            } else {
                _state.postValue(State.Error("Failed to send password reset link", false))
            }
        }
    }

    fun updatePassword(newPassword: String) {
        if (newPassword.isEmpty() && newPassword.length < 6) {
            _state.postValue(State.Error("Password must be atleast 6 characters long", false))
            return
        }

        viewModelScope.launch {
            repo.updatePassword(newPassword)
            _state.postValue(State.Notify("Successfully updated password"))
        }
    }

    sealed interface State {
        data class Error(val error: String?, val shutdown: Boolean) : State
        data class Notify(val message: String) : State
    }
}
