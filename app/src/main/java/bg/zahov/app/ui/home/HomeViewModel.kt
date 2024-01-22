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

    init {
        viewModelScope.launch {
            _state.postValue(State.Loading(true))
            repo.getUser()?.collect {
                _state.postValue(State.Username(it.name))
            } ?: _state.postValue(State.Error("Error loading data"))

            _state.postValue(State.Loading(false))
        }
    }

    sealed interface State {
        object Default : State

        data class Loading(val isLoading: Boolean) : State
        data class Username(val username: String) : State
        data class UserWorkouts(val number: Int) : State

        //FOR CHARTS LATER
        data class Workouts(val workouts: Workout) : State

        data class Error(val message: String) : State
    }
}


