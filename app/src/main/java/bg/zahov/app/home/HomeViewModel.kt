package bg.zahov.app.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.backend.Workout
import bg.zahov.app.common.AuthenticationStateManager
import bg.zahov.app.common.AuthenticationStateObserver
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(), AuthenticationStateObserver {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userName = MutableLiveData<String>()
    private val state = AuthenticationStateManager.getInstance()
    private var repo = UserRepository.getInstance(auth.currentUser!!.uid)
    val userName: LiveData<String> get() = _userName
    private val _numberOfWorkouts = MutableLiveData<Int>()
    val numberOfWorkouts: LiveData<Int> get() = _numberOfWorkouts
    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts
    var flag: Boolean = true

    init {
        viewModelScope.launch {
            UserRepository.getInstance(auth.currentUser!!.uid).getUser()?.collect {
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
        state.addObserver(this)
    }

    override fun onAuthenticationStateChanged(isAuthenticated: Boolean) {
        Log.d("CLEAR", "CLEAR")
        if(!isAuthenticated) {
            state.removeObserver(this)
            onCleared()
        }else{
            Log.d("CLEAR", "another log in")
            auth = FirebaseAuth.getInstance()
            repo = UserRepository.getInstance(auth.currentUser!!.uid)
            Log.d("CLEAR", auth.currentUser?.uid ?: "no")
            state.addObserver(this)
        }
    }
}


