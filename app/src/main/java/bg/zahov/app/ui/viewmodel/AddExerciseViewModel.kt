package bg.zahov.app.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.UserRepository
import bg.zahov.app.data.local.Exercise
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.launch
//FIXME see AuthViewModel comments
class AddExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _bodyPart = MutableLiveData<String>()
    private val _category = MutableLiveData<String>()

    //FIXME please don't use callbacks with Kotlin and coroutines
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

    //FIXME function name does not correspond to what the function actually does
    // Don't use hardcoded strings
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
