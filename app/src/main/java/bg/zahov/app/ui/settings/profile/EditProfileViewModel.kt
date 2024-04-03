package bg.zahov.app.ui.settings.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getUserProvider
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getUserProvider()
    }
    private val serviceErrorHandler by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name


    private val _isUnlocked = MutableLiveData(false)
    val isUnlocked: LiveData<Boolean>
        get() = _isUnlocked


    init {
        viewModelScope.launch {
            try {
                repo.getUser().collect {
                    _name.postValue(it.name)
                }
            } catch (e: CriticalDataNullException) {
                serviceErrorHandler.stopApplication()
            }
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername.isEmpty()) {
            _state.value = State.Notify("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            repo.changeUserName(newUsername)
                .addOnSuccessListener {
                    _name.postValue(newUsername)
                    _state.postValue(State.Notify("Successfully updated username"))
                }
                .addOnFailureListener {
                    _state.postValue(State.Notify("Successfully updated username"))
                }
                .addOnCompleteListener {
                    State.Default
                }

        }
    }

    fun unlockFields(password: String) {
        if (password.isEmpty() || password.length < 6) {
            _state.value = State.Notify("Ensure the password's length is correct!")
            return
        }
        viewModelScope.launch {
            repo.reauthenticate(password)
                .addOnSuccessListener {
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
                    _state.postValue(State.Notify("Failed to send password reset link"))
                }
                .addOnCompleteListener {
                    _state.postValue(State.Default)
                }
        }
    }

    fun updatePassword(newPassword: String) {
        if (newPassword.isEmpty() || newPassword.length < 6) {
            _state.value = State.Notify("Password must be at least 6 characters long")
            return
        }

        viewModelScope.launch {
            repo.updatePassword(newPassword)
                .addOnSuccessListener {
                    _state.postValue(State.Notify("Successfully updated password"))
                }
                .addOnFailureListener {
                    _state.postValue(State.Notify("Failed to update password"))
                }
                .addOnCompleteListener {
                    _state.postValue(State.Default)
                }
        }
    }

    sealed interface State {
        object Default: State
        data class Notify(val message: String) : State
    }
}
