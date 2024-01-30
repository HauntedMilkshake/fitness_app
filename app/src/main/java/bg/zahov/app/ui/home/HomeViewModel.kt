package bg.zahov.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.repository.UserRepositoryImpl
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repo by lazy {
        UserRepositoryImpl.getInstance()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _numberOfWorkouts = MutableLiveData<Int>()
    val numberOfWorkouts: LiveData<Int>
        get() = _numberOfWorkouts

    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts

    init {
        viewModelScope.launch {
            _state.postValue(State.Loading(true))

            repo.getUser()?.collect {
                _userName.postValue(it.name)
                _state.postValue(State.Loading(false))
            } ?: _state.postValue(State.Error("Try reloading the app"))

        }
    }

    sealed interface State {
        object Default : State

        data class Loading(val isLoading: Boolean) : State

        data class Error(val message: String) : State
    }
}