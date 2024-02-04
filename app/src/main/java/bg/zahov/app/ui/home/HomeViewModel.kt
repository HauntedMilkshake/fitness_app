package bg.zahov.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getUserProvider
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getUserProvider()
    }

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
            //potential redundant try catch because of the check in loading ヽ(✿ﾟ▽ﾟ)ノ
            repo.getUser().collect {
                _userName.postValue(it.name)
            }
        }
    }
}