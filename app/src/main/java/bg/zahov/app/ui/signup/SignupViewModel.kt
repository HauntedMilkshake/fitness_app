package bg.zahov.app.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.AuthenticationException
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val auth = AuthenticationImpl.getInstance()

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state


    fun signUp(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ) {
        if (areFieldsEmpty(userName, email, password)) {
            _state.value = State.Error("Do not leave empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Error("Email is not valid")
            return
        }

        if (password != confirmPassword || password.length < 6) {
            _state.value =
                State.Error("Make sure the passwords are matching and at least 6 characters long")
            return
        }

        viewModelScope.launch {
            Log.d("VM", "VM")
            auth.signup(userName, email, password)
            _state.postValue(State.Authentication(true))
        }

    }

    private fun areFieldsEmpty(userName: String?, email: String?, pass: String?) =
        listOf(userName, email, pass).any { it.isNullOrEmpty() }

    sealed interface State {
        object Default : State

        data class Authentication(val isAuthenticated: Boolean) : State

        data class Error(val message: String?) : State
    }
}

