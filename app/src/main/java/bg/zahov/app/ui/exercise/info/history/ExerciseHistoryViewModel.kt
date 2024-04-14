package bg.zahov.app.ui.exercise.info.history

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class ExerciseHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
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
                    it.sortedByDescending { value -> value.date }.let { result ->
                        _state.postValue(State.Data(result))
                    }
                }
            } catch (e: Exception) {
                serviceError.initiateCountdown()
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
        data class Notify(val message: String) : State
    }
}