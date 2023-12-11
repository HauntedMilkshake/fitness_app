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
import bg.zahov.app.utils.checkForChanges
import bg.zahov.app.utils.equalTo
import bg.zahov.app.utils.equalsTo
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
                            initUser()
                            //TODO(initWorkouts())
                            //TODO(initExercises())
                            Log.d(
                                "SYNC",
                                "BEFORE SYNC - Checking settings ${settingsCache?.automaticSync}"
                            )
                                if (settingsCache!!.automaticSync) {
                                    Log.d("SYNC", "Launching sync")
                                    repo.syncToFirestore(
                                        getChangedUserOrNull(),
                                        workoutCache,
                                        exerciseCache,
                                        settingsCache!!
                                    )
                                }
                        }
                    } else {
                        Log.d("SYNC", "COULDN'T SYNC")
                    }
                }
            }, 5000, checkInterval)
    }

    private fun isUserConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private suspend fun getChangedSettingsOrNull(): Settings?  {
        val newSettings = repo.getSettingsSync()
        return if(settingsCache!!.equalTo(newSettings)) {
            settingsCache = newSettings

            settingsCache
        }else{
             null
        }
    }
    private suspend fun getChangedUserOrNull(): User?{
        val newUser = repo.getUserSync()

       return if(userCache!!.equalsTo(newUser)){
            userCache = newUser

            newUser
        }else{
            null
       }
    }
    private suspend fun getChangedWorkoutsOrNull(): List<Workout?>? {
        val newWorkouts = repo.getWorkoutsSync()
        return if()
    }
    private suspend fun getExercises() {
        exerciseCache = repo.getTemplateExercisesSync()
    }
    private suspend fun getWorkouts() {
        workoutCache = repo.getWorkoutsSync()
    }
    private suspend fun initUser() {
        if(userCache == null){
            userCache = repo.getUserSync()
        }
    }
}
