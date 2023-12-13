package bg.zahov.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.realm_db.User
import bg.zahov.app.realm_db.Workout
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.utils.equalTo
import bg.zahov.app.utils.equalsTo
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
    private val timer = Timer()


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

    fun initateSync(context: Context) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.d("SYNC", "Timer is finished")
                if (auth.currentUser != null && auth.currentUser?.uid != null && isUserConnected(context)) {

                    viewModelScope.launch(Dispatchers.IO) {
                        repo.periodicSync()
                    }
                } else {
                    Log.d("SYNC", "COULDN'T SYNC")
                }
            }
        }, 5000, checkInterval)
    }

    private fun isUserConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    //ext functions might not work correct ?
    //TODO(Change it so that we add a way to handle when adding a new template we have a way to check if it is in firestore
    //and either update it or add it
}
