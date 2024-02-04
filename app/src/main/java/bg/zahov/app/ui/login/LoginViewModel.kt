package bg.zahov.app.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getUserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val auth by lazy {
        application.getUserProvider()
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
                val loginTask = auth.login(email, password)
                if (loginTask.isSuccessful) {
                    _state.postValue(State.Authenticated(true))
                } else {
                    _state.postValue(State.Error(loginTask.exception?.message, false))
                }
            } catch (e: IllegalArgumentException) {
                _state.postValue(State.Error("There was a failure with initialization", true))

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
            val result = auth.passwordResetByEmail(email)
            _state.postValue(
                if (result.isSuccessful) State.Notify("Password link sent successfully") else State.Notify(
                    "Couldn't send password link"
                )
            )
        }
    }

    sealed interface State {
        data class Authenticated(val isAuthenticated: Boolean) : State
        data class Error(val eMessage: String?, val shutdown: Boolean) : State
        data class Notify(val nMessage: String) : State
    }
}

