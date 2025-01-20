package bg.zahov.app

import android.app.Application
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.ExercisesTopBarManager
import bg.zahov.app.data.provider.FilterProvider
import bg.zahov.app.data.provider.MeasurementProviderImpl
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.data.provider.SelectableExerciseProvider
import bg.zahov.app.data.provider.ServiceErrorHandlerImpl
import bg.zahov.app.data.provider.SettingsProviderImpl
import bg.zahov.app.data.provider.UserProviderImpl
import bg.zahov.app.data.provider.WorkoutProviderImpl
import bg.zahov.app.data.provider.WorkoutStateManager
import bg.zahov.app.ui.exercise.topbar.ExerciseTopBarManager
import com.google.firebase.Firebase
import com.google.firebase.initialize

object Inject {
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
    val filterProvider by lazy {
        FilterProvider.getInstance()
    }
    val measurementProvider by lazy {
        MeasurementProviderImpl.getInstance()
    }
    val serviceErrorHandler by lazy {
        ServiceErrorHandlerImpl.getInstance()
    }
    val exercisesTopAppHandler by lazy {
        ExercisesTopBarManager.getInstance()
    }
}

class MyApplication : Application() {
    val workoutProvider by lazy {
        WorkoutProviderImpl.getInstance()
    }
    val restTimerProvider by lazy {
        RestTimerProvider.getInstance()
    }
    val serviceErrorHandler by lazy {
        ServiceErrorHandlerImpl.getInstance()
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}