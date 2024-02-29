package bg.zahov.app.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutStatWrapper
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application): AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _state = MutableLiveData<State>(State.Default)
    val state: LiveData<State>
        get() = _state

    private var workouts = listOf<Workout>()
    init {
        viewModelScope.launch {
            _state.postValue(State.Loading(true))
            workoutProvider.getPastWorkouts().collect {
                workouts = it
                _state.postValue(State.Loading(false))

            }
        }
    }
    private fun getVolume(exercises: List<Exercise>): Double {
        var volume = 0.0
        exercises.forEach {exercise ->
            exercise.sets.forEach { set ->
                volume += (set.firstMetric ?: 0).toInt() * (set.secondMetric ?: 0)
            }
        }
        return volume
    }
    private fun getPersonalRecords(exercise: List<Exercise>): Int {
        val prs = 0

        return 0
    }
    private fun isRecord(exercise: Exercise): Boolean {
//        val bestSet = workouts.forEach {workout ->
//            workout.exercises.forEach { exercise ->
//                exercise.sets.forEach {set ->
//                    when(exercise.category) {
//                        Category.AssistedWeight -> {
//                            if(set.type == SetType.DEFAULT.key) {
//
//                            }
//                        }
//                        Category.RepsOnly -> {
//
//                        }
//                        Category.Cardio -> {
//
//                        }
//                        Category.Timed -> {
//
//                        }
//                        else -> {
//
//                        }
//                    }
//                }
//            }
//        }
        return false
    }

    sealed interface State {
        object Default: State
        data class Loading(val isLoading: Boolean): State
        data class Workouts(val workouts: List<WorkoutStatWrapper>): State
    }
}