package bg.zahov.app.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedList
import io.realm.kotlin.notifications.InitialList
import io.realm.kotlin.notifications.UpdatedList
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _userExercises = MutableLiveData<List<Exercise>>()
    val userExercises: LiveData<List<Exercise>> get() = _userExercises

    init {
        getUserExercises()
    }

    private fun getUserExercises() {
        viewModelScope.launch {
            repo.getExercises().collect {changes ->

                _userExercises.postValue(when(changes){
                    is DeletedList -> changes.list
                    is InitialList -> changes.list
                    is UpdatedList -> changes.list
                })

            }
        }
    }
}
