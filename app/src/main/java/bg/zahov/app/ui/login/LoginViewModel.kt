package bg.zahov.app.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getUserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth by lazy {
        application.getUserProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state


    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _state.value = State.Error("Don't leave empty fields", false)
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Error("Email not valid", false)
            return
        }

        viewModelScope.launch {
            try {
                auth.login(email, password)
                    .addOnSuccessListener {
                        _state.value = State.Authenticated(true)
                    }
                    .addOnFailureListener {
                        _state.value = State.Error(it.message, false)
                    }
            } catch (e: IllegalArgumentException) {
                serviceError.stopApplication()
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            _state.value = State.Error("Email must not be empty", false)
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Error("Email is not valid", false)
            return
        }

        viewModelScope.launch {
            auth.passwordResetByEmail(email)
                .addOnSuccessListener {
                    _state.value = State.Notify("Rest password email has been sent")
                }
                .addOnFailureListener {
                    _state.value = State.Error("Couldn't send reset password email!", false)
                }
        }
    }

    sealed interface State {
        data class Authenticated(val isAuthenticated: Boolean) : State
        data class Error(val eMessage: String?, val shutdown: Boolean) : State
        data class Notify(val nMessage: String) : State
    }
}

