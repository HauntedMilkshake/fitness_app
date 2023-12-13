package bg.zahov.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableLiveData<Boolean>()
    private val checkInterval: Long = 30 * 1000 // num of minutes * seconds in a minute * 1000
    private lateinit var repo: UserRepository
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _isAuthenticated.value = firebaseAuth.currentUser != null
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun initiateSync(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (auth.currentUser != null && auth.currentUser?.uid != null && isUserConnected(
                            context
                        )
                    ) {

                        repo = UserRepository(auth.currentUser!!.uid)

                        viewModelScope.launch(Dispatchers.IO) {
                            repo.periodicSync()
                        }

                    }
                }
            }, 5000, checkInterval)
        }
    }

    private fun isUserConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

}
