package bg.zahov.app.ui.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getUserProvider
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(application: Application) : AndroidViewModel(application) {
    private val auth by lazy {
        application.getUserProvider()
    }
    private var stateCounter = 0
    private val _state = MutableStateFlow<State>(State.Default)
    val state = _state.asStateFlow()


    fun signUp(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ) {
        stateCounter += 1

        if (areFieldsEmpty(userName, email, password)) {
            _state.value = State.Notify("Do not leave empty fields", stateCounter)
            return
        }

        if (!email.isEmail()) {
            _state.value = State.Notify("Email is not valid", stateCounter)
            return
        }

        if (password != confirmPassword || password.length < 6) {
            _state.value =
                State.Notify(
                    "Make sure the passwords are matching and at least 6 characters long",
                    stateCounter
                )
            return
        }

        viewModelScope.launch {
            try {
                auth.signup(email, password)
                    .addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            it.user?.uid?.let {
                                auth.createDataSources(userName, it)
                                _state.value = State.Authentication(
                                    "Successfully created account",
                                    stateCounter
                                )

                            } ?: run {
                                _state.value = State.Notify(
                                    "There was an error while attempting to log in", stateCounter
                                )

                            }
                        }
                    }
                    .addOnFailureListener {
                        _state.value = State.Notify(it.message ?: "Unexpected error", stateCounter)

                    }
            } catch (e: CancellationException) {
                _state.value =
                    State.Notify(e.message ?: "There is a problem with the services", stateCounter)
            }

        }

    }

    private fun areFieldsEmpty(userName: String?, email: String?, pass: String?) =
        listOf(userName, email, pass).any { it.isNullOrEmpty() }

    sealed interface State {
        object Default : State
        data class Authentication(val aMessage: String, var stateCounter: Int = 0) : State
        data class Notify(val nMessage: String, var stateCounter: Int? = null) : State
    }
}


