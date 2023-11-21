package bg.zahov.app.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.RealmManager
import bg.zahov.app.realm_db.Workout
import bg.zahov.app.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userName = MutableLiveData<String>()
    private val repo = UserRepository.getInstance()
    val userName: LiveData<String> get() = _userName
    private val _numberOfWorkouts = MutableLiveData<Int>()
    val numberOfWorkouts: LiveData<Int> get() =  _numberOfWorkouts
    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> get() = _userWorkouts

    init {
        viewModelScope.launch {
            repo.getUserHomeInfo(auth.currentUser!!.uid).let {
                _userName.postValue(it.first!!)
                _numberOfWorkouts.postValue(it.second!!)
                _userWorkouts.postValue(it.third!!)
            }
        }
    }


}