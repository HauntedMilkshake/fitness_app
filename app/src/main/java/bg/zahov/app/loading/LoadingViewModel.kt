package bg.zahov.app.loading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.Workout
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoadingViewModel: ViewModel() {
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
            val userInfo = repo.getUserHomeInfo()
            _userName.postValue(userInfo.first!!)
            _numberOfWorkouts.postValue(userInfo.second!!)
            _userWorkouts.postValue(userInfo.third!!)
        }
    }
}