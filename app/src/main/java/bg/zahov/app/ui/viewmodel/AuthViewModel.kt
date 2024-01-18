package bg.zahov.app.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

//FIXME This VM does a lot. I will add a bigger comment here and refer to this class in other places
// where the comments are relevant.
// 1. I see that you use FirebaseAuth all over the project. This is not a good idea - what if you
// decide to switch to another identity provider? A common approach is to define an authentication
// service (not an Android Service) interface with methods for login, sign-up, logout, account deletion and so on and use
// that when these functionalities are needed (for this project in the login/signup and settings
// feature packages). Once user access is granted, the auth service should update the user in UserRepository
// with the current user - this should be the only component that updates the values in UserRepository.
// All other application components should only care about the current user provided by UserRepository.
// A good practice is to use a sealed class for the User entity that has a NoUser subclass so you always
// have a non-nullable user in the app.
// 2. This is not a good place and way to handle periodic data sync. Check out WorkManager APIs https://developer.android.com/develop/background-work/background-tasks/persistent
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userId: String? = null
    private val _isAuthenticated = MutableLiveData<Boolean>()
    private val checkInterval: Long = 15 * 60 * 1000 // num of minutes * seconds in a minute * 1000
    private var syncTask: TimerTask? = null
    private var repo: UserRepository? = null
    private val authStateListener = FirebaseAuth.AuthStateListener {
        userId = it.currentUser?.uid
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated
    //FIXME this whole function should be scrapped and replaced with something more robust,
    // but I will leave some comments so you get an idea of what's wrong
    fun initiateSync(context: Context) {
        //FIXME it's not a good idea to mix coroutines with Java threading APIs. Also
        viewModelScope.launch(Dispatchers.Default) {
            syncTask?.cancel()
            syncTask = object : TimerTask() {
                override fun run() {
                    //FIXME you are leaking your context here by passing the reference to the anonymous
                    // TimerTask class. This task will be run on a separate thread
                    if (auth.currentUser != null && userId != null && isUserConnected(context)) {

                        //might be redundant
                        userId?.let {
                            repo = UserRepository.getInstance(it)

                        }
                        //FIXME you are leaking your VM here by passing the reference of the viewModelScope
                        // to the anonymous TimerTask class
                        viewModelScope.launch {
//                            repo?.periodicSync()
                        }
                    }
                }
            }
            //FIXME Timer creation is expensive - a new thread is created when the object is constructed
            Timer().scheduleAtFixedRate(syncTask, 12000, checkInterval)
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.Main) {
            userId?.let {
                repo = UserRepository.getInstance(it)
            }
            cancelSync().join()
            _isAuthenticated.value = false
            repo?.deleteRealm()
            auth.signOut()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            userId?.let {
                repo = UserRepository.getInstance(it)
            }
            cancelSync().join()
            _isAuthenticated.value = false
            repo?.deleteUser(auth)
            auth.signOut()
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
