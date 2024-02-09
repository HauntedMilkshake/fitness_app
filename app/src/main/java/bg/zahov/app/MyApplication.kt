package bg.zahov.app

import android.app.Application
import bg.zahov.app.data.provider.SettingsProviderImpl
import bg.zahov.app.data.provider.UserProviderImpl
import bg.zahov.app.data.provider.WorkoutProviderImpl
import bg.zahov.app.data.provider.WorkoutStateManager
import com.google.firebase.Firebase
import com.google.firebase.initialize

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
    val workoutState by lazy {
        WorkoutStateManager.getInstance()
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}

fun Application.getUserProvider() = (this as MyApplication).userProvider
fun Application.getSettingsProvider() = (this as MyApplication).settingsProvider
fun Application.getWorkoutProvider() = (this as MyApplication).workoutProvider
fun Application.getWorkoutStateManager() = (this as MyApplication).workoutState

