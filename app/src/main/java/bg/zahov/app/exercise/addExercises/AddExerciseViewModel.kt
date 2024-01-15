package bg.zahov.app.exercise.addExercises

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.BodyPart
import bg.zahov.app.data.Category
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.backend.Exercise
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.launch

class AddExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _bodyPart = MutableLiveData<String>()
    private val _category = MutableLiveData<String>()

    fun addExercise(exerciseTitle: String?, callback: (Boolean, String?) -> Unit) {
        if (exerciseTitle.isNullOrEmpty() || _category.value.isNullOrEmpty() || _bodyPart.value.isNullOrEmpty()) {
            callback(false, "Do not leave empty fields!")
            return
        }

        viewModelScope.launch {
            repo.addExercise(Exercise().apply {
                bodyPart = _bodyPart.value
                category = _category.value
                exerciseName = exerciseTitle
                isTemplate = true
                sets = realmListOf()
            })
            callback(true, "Successfully added exercise :)")
        }
    }

    fun buildExercise(title: String, info: String) {
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
