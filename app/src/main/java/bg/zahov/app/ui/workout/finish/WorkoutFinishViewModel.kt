package bg.zahov.app.ui.workout.finish

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch

class WorkoutFinishViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _workout = MutableLiveData<Workout>()
    val workout: LiveData<Workout>
        get() = _workout

    private val _workoutCount = MutableLiveData<String>()
    val workoutCount: LiveData<String>
        get() = _workoutCount

    init {
        _workout.value = workoutProvider.getLastWorkout()
        viewModelScope.launch {
            workoutProvider.getPastWorkouts().collect {
                _workoutCount.postValue(" This is your workout number ${it.size}!")
            }
        }
    }
}
