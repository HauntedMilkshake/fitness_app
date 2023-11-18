package bg.zahov.app.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.RealmManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName
    private val _userWorkouts = MutableLiveData<Int>()
    val userWorkouts: LiveData<Int> get() = _userWorkouts

    init {
        viewModelScope.launch {
            RealmManager.getInstance().getUsernameAndNumberOfWorkouts(auth.currentUser!!.uid)
                .let {
                    _userName.postValue(it?.first ?: "no name")
                    _userWorkouts.postValue(it?.second ?: 0)
                }
        }
    }

}