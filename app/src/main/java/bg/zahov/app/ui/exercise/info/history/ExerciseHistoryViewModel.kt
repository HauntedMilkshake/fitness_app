package bg.zahov.app.ui.exercise.info.history

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.getOneRepMaxes
import bg.zahov.app.util.toFormattedString
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ExerciseHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _state = MutableLiveData<State>(State.Default)
    val state: LiveData<State>
        get() = _state
    private lateinit var job: Job
    fun initData() {
        job = viewModelScope.launch(NonCancellable) {
            _state.postValue(State.Loading(View.VISIBLE))
            try {
                workoutProvider.getExerciseHistory().collect {
                    _state.postValue(State.Data(it))
                }
            } catch (e: Exception) {
                _state.postValue(
                    if (e is CriticalDataNullException) State.Error(true) else State.Notify(
                        "Please try reloading the clicked exercise"
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    sealed interface State {
        object Default : State
        data class Data(val data: List<ExerciseHistoryInfo>) : State
        data class Loading(val loadingVisibility: Int) : State
        data class Error(val shutdown: Boolean) : State
        data class Notify(val message: String) : State
    }
}