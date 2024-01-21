package bg.zahov.app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.remote.AuthenticationImpl
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var auth = AuthenticationImpl.getInstance()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _state.value = State.Error("Dont leave empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Error("Email not valid")
            return
        }

        viewModelScope.launch {
            try {
                auth.login(email, password)
                _state.postValue(State.Authenticated(true))
            } catch (e: AuthenticationException) {
                _state.postValue(State.Error(e.message))
            }
        }

    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            _state.value = State.Error("Empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Error("Email is not valid")
            return
        }

        viewModelScope.launch {
            try {
                auth.passwordReset(email)
                _state.postValue(State.ForgotPasswordLinkSent("Password link sent"))
            } catch (e: AuthenticationException) {
                _state.value = State.Error(e.message)
            }
        }

    }

    sealed interface State {
        object Default : State

        data class Authenticated(val isAuthenticated: Boolean) : State
        data class Error(val errorMessage: String?) : State
        data class ForgotPasswordLinkSent(val message: String) : State
    }
}
