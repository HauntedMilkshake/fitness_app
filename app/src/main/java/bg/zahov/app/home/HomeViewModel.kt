package bg.zahov.app.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.realm_db.Workout
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userName = MutableLiveData<String>()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    val userName: LiveData<String> get() = _userName
    private val _numberOfWorkouts = MutableLiveData<Int>()
    val numberOfWorkouts: LiveData<Int> get() = _numberOfWorkouts
    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts

    init {
        viewModelScope.launch {
            Log.d("INIT", repo.getUser().toString())
            repo.getUser()!!.collect {
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
//                    is InitialResults -> {
//                        _userName.postValue(it.list[0].username)
//                        _numberOfWorkouts.postValue(it.list[0].numberOfWorkouts)
//                    }
//                    is UpdatedResults -> {
//                        _userName.postValue(it.list[0].username)
//                        _numberOfWorkouts.postValue(it.list[0].numberOfWorkouts)
//                    }
                }
            }
        }
    }

}


