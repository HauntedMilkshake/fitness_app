package bg.zahov.app

import android.app.Application
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.data.provider.SelectableExerciseProvider
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
    val selectedExerciseProvider by lazy {
        SelectableExerciseProvider.getInstance()
    }
    val replaceableExerciseProvider by lazy {
        ReplaceableExerciseProvider.getInstance()
    }
    val workoutAddedExerciseProvider by lazy {
        AddExerciseToWorkoutProvider.getInstance()
    }
    val restTimerProvider by lazy {
        RestTimerProvider.getInstance()
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
fun Application.getSelectableExerciseProvider() = (this as MyApplication).selectedExerciseProvider
fun Application.getReplaceableExerciseProvider() = (this as MyApplication).replaceableExerciseProvider
fun Application.getAddExerciseToWorkoutProvider() = (this as MyApplication).workoutAddedExerciseProvider
fun Application.getRestTimerProvider() = (this as MyApplication).restTimerProvider

