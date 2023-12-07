package bg.zahov.app.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _userExercises = MutableLiveData<List<Exercise>>()
    val userExercises: LiveData<List<Exercise>> get() = _userExercises

    init {
//        getUserExercises()
    }

//    private fun getUserExercises() {
//        viewModelScope.launch {
//            repo.getUser().collect {
//                when (it) {
//                    is DeletedObject -> {}
//                    is InitialObject -> _userExercises.postValue(it.obj.customExercises)
//                    is UpdatedObject -> _userExercises.postValue(it.obj.customExercises)
//                }
//            }
//        }
//    }
}
//TODO(Updated object add only new exercises to list)
