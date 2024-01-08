package bg.zahov.app.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.backend.Workout
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userName = MutableLiveData<String>()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    val userName: LiveData<String> get() = _userName
    private val _numberOfWorkouts = MutableLiveData<Int>()
    val numberOfWorkouts: LiveData<Int> get() = _numberOfWorkouts
    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts

    init {
        viewModelScope.launch {
            repo.getUser()?.collect {
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

    override fun onCleared() {
        super.onCleared()
        Log.d("ClEARED", "HOME")
    }
}


