package bg.zahov.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class WorkoutViewModel(application: Application): AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val _state = MutableLiveData<State>()

    sealed interface State {

    }
}