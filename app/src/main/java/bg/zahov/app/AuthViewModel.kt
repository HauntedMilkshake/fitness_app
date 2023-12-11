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
                            initSettingsCache()

                            if (settingsCache!!.automaticSync) {

                                initWorkoutCache()
                                initExerciseCache()

                                Log.d("SYNC", "Launching sync")
                                repo.syncToFirestore(
                                    getChangedUserOrNull(),
                                    getChangedWorkoutsOrNull(),
                                    getChangedExercisesOrNull(),
                                    getChangedSettingsOrNull()
                                )
                            }
                            Log.d(
                                "SYNC",
                                "BEFORE SYNC - Checking settings ${settingsCache?.automaticSync}"
                            )





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

        initSettingsCache()

        val newSettings = repo.getSettingsSync()
        return if(settingsCache!!.equalTo(newSettings)) {
            null
        }else{
            settingsCache = newSettings

            settingsCache
        }
    }
    private suspend fun getChangedUserOrNull(): User?{

        initUserCache()


        val newUser = repo.getUserSync()

       return if(userCache!!.equalsTo(newUser)){
            null
        }else{
           userCache = newUser

           newUser
       }
    }
    private suspend fun getChangedWorkoutsOrNull(): List<Workout?>? {

        initWorkoutCache()

        val currWorkouts = repo.getWorkoutsSync()
        val newWorkouts = mutableListOf<Workout>()

        workoutCache.forEach {wCache ->
            currWorkouts.forEach {currWorkouts ->
                if(!(wCache.equalsTo(currWorkouts))){
                    newWorkouts.add(currWorkouts)
                }
            }
        }

        return if(newWorkouts.size == 0){
            null
        }else{
            newWorkouts
        }
    }
    private suspend fun getChangedExercisesOrNull(): List<Exercise?>? {

        initExerciseCache()


        val currTemplateExercises = repo.getTemplateExercisesSync()
        val newExercises = mutableListOf<Exercise>()

        exerciseCache.forEach { cExercise ->
            currTemplateExercises.forEach {currExercise ->
                if(!(cExercise.equalsTo(currExercise))){
                    newExercises.add(currExercise)
                }
            }
        }

        return if(newExercises.size == 0){
            null
        }else{
            newExercises
        }
    }
    private suspend fun initUserCache() {
        if(userCache == null){
            userCache = repo.getSyncUserFromFirestore()
            Log.d("INFO", "USERINFO -> ${userCache?.username}")
        }
    }
    private suspend fun initSettingsCache(){
        if(settingsCache == null){
            settingsCache = repo.getSyncSettingsFromFirestore()
            Log.d("INFO", "SETTINGSINFO -> ${settingsCache?.language}")
        }
    }
    private suspend fun initWorkoutCache(){
        if(workoutCache.isEmpty()){
            workoutCache = repo.getSyncWorkoutsFromFirestore()
            Log.d("INFO", "WORKOUTINFO -> ${workoutCache.forEach { it.toString() }}")
        }
    }
    private suspend fun initExerciseCache(){
        if(exerciseCache.isEmpty()){
            exerciseCache = repo.getSyncExercisesFromFirestore()
            Log.d("INFO", "EXERCISEINFO  -> ${workoutCache.forEach { it.toString() }}")

        }
    }
}
