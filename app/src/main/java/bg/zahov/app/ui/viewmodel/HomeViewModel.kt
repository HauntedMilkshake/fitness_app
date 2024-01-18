package bg.zahov.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.UserRepository
import bg.zahov.app.data.local.Workout
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.launch

//FIXME check comments in AuthViewModel
class HomeViewModel : ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userName = MutableLiveData<String>()
    private val repo by lazy {
        auth.currentUser?.uid?.let {
            UserRepository.getInstance(it)
        }
    }
    val userName: LiveData<String> get() = _userName
    private val _numberOfWorkouts = MutableLiveData<Int>()
    val numberOfWorkouts: LiveData<Int> get() = _numberOfWorkouts
    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts

    init {
        viewModelScope.launch {
            repo?.getUser()?.collect {
                when (it) {
                    is DeletedObject -> {
                        _userName.postValue(it.obj?.username)
                        _numberOfWorkouts.postValue(it.obj?.numberOfWorkouts)
                    }

                    is InitialObject -> {
                        _userName.postValue(it.obj.username)
                        _numberOfWorkouts.postValue(it.obj.numberOfWorkouts)
                    }

                    is UpdatedObject -> {
                        _userName.postValue(it.obj.username)
                        _numberOfWorkouts.postValue(it.obj.numberOfWorkouts)
                    }

                }
            }
        }
    }
}


