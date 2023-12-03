package bg.zahov.app.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.realm_db.Exercise
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ExerciseViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _userExercises = MutableLiveData<List<Exercise>>()
    val userExercises: LiveData<List<Exercise>> get() = _userExercises
    init{
    }
    fun refreshUserExercises(){
        if(auth.uid != null){
            viewModelScope.launch {
                if(_userExercises.value.isNullOrEmpty()){
                    _userExercises.postValue(repo.getUserExercises())
                }else{
                    _userExercises.value = listOf()
                    _userExercises.postValue(repo.getUserExercises())
                }
            }
        }else{
            auth.signOut()
        }
    }
}