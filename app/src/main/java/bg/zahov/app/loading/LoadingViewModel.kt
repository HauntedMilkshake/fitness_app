package bg.zahov.app.loading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingViewModel: ViewModel(){
    private val auth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    fun startAnimations(duration: Long) {
        viewModelScope.launch {
            delay(duration)
            _isAuthenticated.postValue(auth.currentUser?.uid != null)
        }
    }
}