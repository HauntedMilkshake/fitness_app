package bg.zahov.app.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var auth = AuthenticationImpl.getInstance()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _state.value = State.Notify("Don't leave empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Notify("Email not valid")
            return
        }

        viewModelScope.launch {
                Log.d("login", "calling from vm")
                auth.login(email, password)
                _state.postValue(State.Authenticated(true))
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            _state.value = State.Notify("Empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Notify("Email is not valid")
            return
        }

        viewModelScope.launch {
                val result = auth.passwordResetByEmail(email)
                _state.postValue(if(result) State.Notify("Password link sent successfully") else State.Notify("Couldn't send password link"))
        }
    }

    sealed interface State {
        object Default : State
        data class Authenticated(val isAuthenticated: Boolean) : State
        data class Notify(val message: String) : State
    }
}
