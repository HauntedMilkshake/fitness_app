package bg.zahov.app.exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.Category
import bg.zahov.app.data.ExerciseType
import bg.zahov.app.mediators.SettingsManager
import bg.zahov.app.mediators.UserRepository
import bg.zahov.app.realm_db.Exercise
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application): AndroidViewModel(application) {
    private val realm = UserRepository.getInstance()
        private val auth = FirebaseAuth.getInstance()
    //might not even need this to be a live data
    private val _exercise = MutableLiveData<Exercise>()
    //will be used later for checking sync option
    private val storage = SettingsManager.getInstance(application)

    //might make it boolean or return a callback to know if we have successfully added an exercise
    //would be good to create ?: adding a blank exercise
    fun addExercise(exerciseTitle: String ){
        _exercise.value!!.exerciseName = exerciseTitle
        auth.uid?.let{
            viewModelScope.launch {
                realm.addExercise(it, _exercise.value!!)
            }
        }
    }
    private fun buildExercise(title: String, info: Any){
        when (title) {
            "Category" -> {
                _exercise.value!!.bodyPart = Category.valueOf(info.toString()).toString()
            }
            "ExerciseType" -> {
                _exercise.value!!.category = ExerciseType.valueOf(info.toString()).toString()
            }
            else -> {
                //TODO(Add proper handling)
                return
            }
        }
    }
    fun

}
