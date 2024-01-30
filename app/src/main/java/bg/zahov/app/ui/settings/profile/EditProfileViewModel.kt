package bg.zahov.app.ui.settings.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.data.repository.UserRepositoryImpl
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = UserRepositoryImpl.getInstance()
    private val auth = AuthenticationImpl.getInstance()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private var userPassword: String? = null

    //
    init {
        viewModelScope.launch {
            repo.getUser()?.collect {
                _state.postValue(State.Username(it.name))
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            if (newUsername.isNotEmpty()) {
                repo.changeUserName(newUsername)
                _state.postValue(State.Username(newUsername))
            } else {
                _state.postValue(State.Error("Username cannot be empty"))
            }
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            auth.updateEmail(newEmail)
        }
    }

    fun unlockFields(password: String) {
        viewModelScope.launch {
            if (password.isNotEmpty() && password.length >= 6) {
                auth.reauthenticate(password).collect {
                    //TODO(ADD Message here)
                    _state.postValue(State.Unlocked(it))
                }
            } else {
                _state.postValue(State.Unlocked(false))
            }
        }
    }

    fun sendPasswordResetLink() {
        viewModelScope.launch {
            auth.passwordResetForLoggedUser()
            _state.postValue(State.Notify("Successfully updated password"))
        }
    }

    fun updatePassword(newPassword: String) {
        if (newPassword.isNotEmpty() && newPassword.length >= 6) {
            viewModelScope.launch {
                auth.updatePassword(newPassword)
                _state.postValue(State.Notify("Successfully updated password"))
            }
        }
    }

    sealed interface State {
        object Default : State

        data class Unlocked(val isUnlocked: Boolean) : State
        data class Error(val error: String) : State
        data class Username(val username: String) : State
        data class Email(val email: String) : State
        data class Notify(val message: String) : State
    }
}
