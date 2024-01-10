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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userId: String? = null
    private val _isAuthenticated = MutableLiveData<Boolean>()
    private val checkInterval: Long = 15 * 60 * 1000 // num of minutes * seconds in a minute * 1000
    private var syncTask: TimerTask? = null
    private var repo: UserRepository? = null
    private val authStateListener = FirebaseAuth.AuthStateListener {
        userId = if(it.currentUser != null) it.currentUser?.uid else null
    }
    init {
        auth.addAuthStateListener(authStateListener)
    }
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated
    fun initiateSync(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            syncTask?.cancel()
            syncTask = object : TimerTask() {
                override fun run() {
                    if (auth.currentUser != null && userId != null && isUserConnected(context)) {

                        Log.d("ID", userId.toString())
                        userId?.let{
                            repo = UserRepository.getInstance(it)
                        }

                        viewModelScope.launch(Dispatchers.IO) {
//                            repo?.let{
//                                it.periodicSync()
//                            }
                        }
                    }
                }
            }

            Timer().scheduleAtFixedRate(syncTask, 12000, checkInterval)
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.Main) {
            userId?.let {
                repo = UserRepository.getInstance(it)
            }
            Log.d("REPO", if(repo == null) "kur" else "nekur")
            cancelSync().join()
            _isAuthenticated.value = false
            repo?.deleteRealm()
            repo?.invalidate()
            auth.signOut()
        }
    }
    fun deleteAccount() {
        viewModelScope.launch {
            cancelSync()
            repo?.deleteUser(auth)
            auth.signOut()
            _isAuthenticated.value = false
            repo?.invalidate()
        }
    }
    private fun cancelSync(): Job {
        return viewModelScope.launch {
            syncTask?.cancel()
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
