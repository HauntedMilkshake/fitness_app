package bg.zahov.app.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.backend.Workout
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _templates = MutableLiveData<List<Workout>>(listOf())
    val templates: LiveData<List<Workout>> get() = _templates

    init {
        viewModelScope.launch {
            repo.getTemplateWorkouts()?.collect {
                when (it) {
                    is InitialResults -> _templates.postValue(it.list)
                    is UpdatedResults -> _templates.postValue(it.list)
                    else -> _templates.postValue(it.list)
                }
            }
        }
    }

}