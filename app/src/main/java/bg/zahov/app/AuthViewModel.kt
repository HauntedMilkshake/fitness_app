package bg.zahov.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableLiveData<Boolean>()
    private val checkInterval: Long = 300000 // 5m
    private lateinit var repository: UserRepository
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

    fun syncCheck(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                if (auth.currentUser != null && auth.currentUser?.uid != null) {
                    if (isUserConnected(context)) {
                        delay(2000)

                        repository = UserRepository(auth.currentUser!!.uid)

                        repository.syncToFirestore()
                    }
                }
                delay(checkInterval)
                Log.d("SYNCCHECK", "CHECK")
            }
        }
    }

    private fun isUserConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
