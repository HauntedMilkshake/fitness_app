package bg.zahov.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth by lazy {
        application.getUserProvider()
    }
    private val workoutProvider by lazy {

    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            if (auth.isAuthenticated()) {
                auth.initDataSources()

            }
        }
        _state.value = State.Authenticated(auth.isAuthenticated())
    }

    sealed interface State {
        data class Authenticated(val isAuthenticated: Boolean) : State
    }
}
