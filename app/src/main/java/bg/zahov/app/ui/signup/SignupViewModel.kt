package bg.zahov.app.ui.signup

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getUserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel(application: Application) : AndroidViewModel(application) {
    private val auth by lazy {
        application.getUserProvider()
    }

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
            _state.value = State.Notify("Do not leave empty fields")
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Notify("Email is not valid")
            return
        }

        if (password != confirmPassword || password.length < 6) {
            _state.value =
                State.Notify("Make sure the passwords are matching and at least 6 characters long")
            return
        }

        viewModelScope.launch {
            try {
                auth.signup(email, password)
                    .addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            it.user?.uid?.let {
                                auth.createDataSources(userName, it)
                                _state.postValue(State.Authentication(true))
                            } ?: _state.postValue(
                                State.Error(
                                    "There was an error while attempting to log in",
                                    false
                                )
                            )
                        }
                        Log.d("posting state", "posting...")
                    }
                    .addOnFailureListener {
                        _state.value = State.Error(it.message, false)
                    }
            } catch (e: CancellationException) {
                _state.postValue(State.Error(e.message, true))
            }

        }

    }

    private fun areFieldsEmpty(userName: String?, email: String?, pass: String?) =
        listOf(userName, email, pass).any { it.isNullOrEmpty() }

    sealed interface State {
        data class Authentication(val isAuthenticated: Boolean) : State
        data class Error(val eMessage: String?, val shutdown: Boolean) : State
        data class Notify(val nMessage: String) : State
    }
}


