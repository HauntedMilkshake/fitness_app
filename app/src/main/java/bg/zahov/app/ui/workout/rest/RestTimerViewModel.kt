package bg.zahov.app.ui.workout.rest

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class RestTimerViewModel(application: Application): AndroidViewModel(application) {

    sealed interface State {
        object Default: State
    }
}