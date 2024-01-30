package bg.zahov.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.repository.AuthenticationImpl
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = AuthenticationImpl.getInstance()
    private val _state = MutableLiveData<State>()
    val state : LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            if(auth.isAuthenticated()) {
                auth.initDataSources()
            }
        }
        _state.value = State.Authenticated(auth.isAuthenticated())
    }
    sealed interface State {
        object Default : State
        data class Authenticated(val isAuthenticated: Boolean) : State
    }
}
