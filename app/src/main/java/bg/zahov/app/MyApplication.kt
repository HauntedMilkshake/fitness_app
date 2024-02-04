package bg.zahov.app

import android.app.Application
import android.util.Log
import bg.zahov.app.data.provider.SettingsProviderImpl
import bg.zahov.app.data.provider.UserProviderImpl
import bg.zahov.app.data.provider.WorkoutProviderImpl
import com.google.firebase.Firebase
import com.google.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    val userProvider by lazy {
            UserProviderImpl.getInstance()
    }
    val settingsProvider by lazy {
        SettingsProviderImpl.getInstance()
    }
    val workoutProvider by lazy {
        WorkoutProviderImpl.getInstance()
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}
fun Application.getUserProvider() = (this as MyApplication).userProvider
fun Application.getSettingsProvider() = (this as MyApplication).settingsProvider
fun Application.getWorkoutProvider() = (this as MyApplication).workoutProvider

