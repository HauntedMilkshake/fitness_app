package bg.zahov.app.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _templates = MutableLiveData<List<Workout>>(listOf())
    val templates: LiveData<List<Workout>> get() = _templates

    init {
        viewModelScope.launch {
//            repo.getTemplateWorkouts()?.collect {
//                when (it) {
//                    is InitialResults -> _templates.postValue(it.list)
//                    is UpdatedResults -> _templates.postValue(it.list)
//                    else -> _templates.postValue(it.list)
//                }
//            }
        }
    }

}