package bg.zahov.app.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.mediators.UserRepository
import bg.zahov.app.realm_db.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.launch

class ExerciseViewModel: ViewModel() {
    private val localDB = UserRepository.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _userExercises = MutableLiveData<List<Exercise>>()
    val userExercises: LiveData<List<Exercise>> get() = _userExercises
    init{
    }
    fun refreshUserExercises(){
        if(auth.uid != null){
            viewModelScope.launch {
                if(_userExercises.value.isNullOrEmpty()){
                    _userExercises.postValue(localDB.getUserExercises(auth.uid!!))
                }else{
                    _userExercises.value = listOf()
                    _userExercises.postValue(localDB.getUserExercises(auth.uid!!))
                }
            }
        }else{
            auth.signOut()
        }
    }
}