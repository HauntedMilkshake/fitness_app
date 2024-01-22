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
            _state.value = State.AuthError("Don't leave empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.AuthError("Email not valid")
            return
        }

        viewModelScope.launch {
            try {
                auth.login(email, password)
                _state.postValue(State.Authenticated(true))
            } catch (e: AuthenticationException) {
                _state.postValue(State.AuthError(e.message))
            }
        }

    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            _state.value = State.PasswordLinkError("Empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.PasswordLinkError("Email is not valid")
            return
        }

        viewModelScope.launch {
            try {
                auth.passwordReset(email)
                _state.postValue(State.PasswordLink("Password link sent"))
            } catch (e: AuthenticationException) {
                _state.value = State.PasswordLinkError(
                    e.message ?: "Error with sending link, try again later."
                )
            }
        }

    }

    sealed interface State {
        object Default : State

        data class Authenticated(val isAuthenticated: Boolean) : State
        data class AuthError(val errorMessage: String?) : State

        data class PasswordLinkError(val errorMessage: String) : State
        data class PasswordLink(val message: String) : State
    }
}
