package bg.zahov.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.repository.UserRepositoryImpl
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _userName = MutableLiveData<String>()
    private val repo by lazy {
        UserRepositoryImpl.getInstance()
    }
    val userName: LiveData<String> get() = _userName
    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts

    init {
        viewModelScope.launch {
            repo.getUser().collect {
                //TODO(Custom type from discord post value here)
            }
        }
    }
}


