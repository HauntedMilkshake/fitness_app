package bg.zahov.app.exercise

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.BodyPart
import bg.zahov.app.data.Category
import bg.zahov.app.mediators.SettingsManager
import bg.zahov.app.mediators.UserRepository
import bg.zahov.app.realm_db.Exercise
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.launch

class AddExerciseViewModel(application: Application): AndroidViewModel(application) {
    private val realm = UserRepository.getInstance()
    private val auth = FirebaseAuth.getInstance()
    //might not even need this to be a live data
    private val _bodyPart = MutableLiveData<String>()
    private val _category = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    //will be used later for checking sync option
    private val storage = SettingsManager.getInstance(application)
    //might make it boolean or return a callback to know if we have successfully added an exercise
    //would be good to create ?: adding a blank exercise
    fun addExercise(exerciseTitle: String ){
        if(exerciseTitle.isNullOrEmpty()){
            Toast.makeText(getApplication(), "Please don't leave title empty", Toast.LENGTH_SHORT).show()
        }else{
            auth.uid?.let{
                viewModelScope.launch {
                    realm.addExercise(it,Exercise().apply {
                        bodyPart = _bodyPart.value
                        category = _category.value
                        exerciseName = _title.value
                        sets = realmListOf()
                    })
                }
            }
        }
    }
    fun buildExercise(title: String, info: String){
        when (title) {
            "Body part" -> {
                _bodyPart.value = BodyPart.valueOf(info).name
            }
            "Category" -> {
                _category.value = Category.valueOf(info).name
            }
            else -> {
                return
            }
        }
    }
    fun getCurrBodyPart() = _bodyPart.value
    fun getCurrCategory() = _category.value
}
