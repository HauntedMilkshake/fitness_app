package bg.zahov.app.ui.history

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>(State.Default)
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            _state.postValue(State.Loading(View.VISIBLE, View.GONE))
            workoutProvider.getPastWorkouts().collect {
                try {
                    _state.postValue(State.Data(it.sortedByDescending { item -> item.date }))
                } catch (e: CriticalDataNullException) {
                    Log.d("e", e.message ?: "")
                    serviceError.initiateCountdown()
                }
            }
        }
    }

    sealed interface State {
        object Default : State
        data class Loading(val loadingVisibility: Int, val workoutsVisibility: Int) : State
        data class Data(val workouts: List<Workout>) : State
    }
}