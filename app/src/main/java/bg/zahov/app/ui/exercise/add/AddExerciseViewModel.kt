package bg.zahov.app.ui.exercise.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import kotlinx.coroutines.launch
//FIXME see AuthViewModel comments
class AddExerciseViewModel : ViewModel() {
    private val workoutRepo = WorkoutRepositoryImpl.getInstance()
    private val _bodyPart = MutableLiveData<String>()
    private val _category = MutableLiveData<String>()

    fun addExercise(exerciseTitle: String?) {
        if (exerciseTitle.isNullOrEmpty() || _category.value.isNullOrEmpty() || _bodyPart.value.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            //
        }
    }

    fun setBodyPart(info: String) {
        _bodyPart.value = BodyPart.fromKey(info)
    }
    fun setCategory(info: String) {
        _category.value = Category.fromKey(info)
    }

    fun getCurrBodyPart() = _bodyPart.value
    fun getCurrCategory() = _category.value
}
