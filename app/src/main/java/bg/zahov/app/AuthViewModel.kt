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
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableLiveData<Boolean>()
    private val checkInterval: Long =  30 * 1000 // num of minutes * seconds in a minute * 1000
    private lateinit var repo: UserRepository
    private var userCache: User? = null
    private var settingsCache: Settings? = null
    private var workoutCache: List<Workout> = listOf()
    private var exerciseCache: List<Exercise> = listOf()
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

    fun syncCheck(context: Context) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Log.d("SYNC", "BEFORE SYNC")
                    if (auth.currentUser != null && auth.currentUser?.uid != null && isUserConnected(context)) {
                        repo = UserRepository(auth.uid!!)
                        Log.d("SYNC", "BEFORE SYNC - Inside if condition ${auth.uid}")

                        viewModelScope.launch(Dispatchers.IO) {
                            Log.d(
                                "SYNC",
                                "BEFORE SYNC - Funny"
                            )
                            getCurrUser()
                            getCurrSettings()
                            getWorkouts()
                            getExerciseTemplates()

                            Log.d(
                                "SYNC",
                                "BEFORE SYNC - Inside if condition ${settingsCache?.automaticSync}"
                            )

                            settingsCache?.let {
                                if (it.automaticSync) {
                                    Log.d("SYNC", "Launching sync")
                                    repo.syncToFirestore(
                                        userCache!!,
                                        workoutCache,
                                        exerciseCache,
                                        settingsCache!!
                                    )
                                }
                            }
                        }
                    } else {
                        Log.d("SYNC", "COULDN'T SYNC")
                    }
                }
            }, 0, checkInterval)
    }

    private fun isUserConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
    private suspend fun getCurrUser() {
        withContext(Dispatchers.IO) {
            repo.getUser().collect {
                when (it) {
                    is DeletedObject -> {
                        Log.d("SYNC", "User deleted")
                    }
                    is InitialObject -> {
                        Log.d("SYNC", "Initial User object received: ${it.obj}")
                        if (userCache == null) userCache = it.obj
                    }
                    is UpdatedObject -> {
                        Log.d("SYNC", "User object updated: ${it.obj}")
                        userCache = it.obj
                    }
                }
            }
        }
    }


    private suspend fun getCurrSettings() {
        withContext(Dispatchers.IO) {
            repo.getSettings().collect {
                when(it){
                    is DeletedObject -> {}
                    is InitialObject -> if(settingsCache == null) settingsCache = it.obj
                    is UpdatedObject -> settingsCache = it.obj
                }
            }
            Log.d("SYNC", "SETTINGS -> ${settingsCache?.automaticSync}")
        }
    }

    private suspend fun getWorkouts() {
        withContext(Dispatchers.IO) {
            repo.getAllWorkouts().collect {
                when(it){
                    is InitialResults -> if(workoutCache.isEmpty()) workoutCache = it.list
                    is UpdatedResults -> workoutCache = it.list
                }
            }
        }
    }

    private suspend fun getExerciseTemplates() {
        withContext(Dispatchers.IO) {
            repo.getTemplateExercises().collect {
                when(it){
                    is InitialResults -> if(exerciseCache.isEmpty()) exerciseCache = it.list
                    is UpdatedResults -> exerciseCache = it.list
                }
            }
        }
    }

}
