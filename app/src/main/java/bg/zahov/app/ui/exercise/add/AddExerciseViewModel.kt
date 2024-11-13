package bg.zahov.app.ui.exercise.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.fitness.app.R
import kotlinx.coroutines.launch
class AddExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private var exercises: List<Exercise> = emptyList()

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _category = MutableLiveData("")
    val category: LiveData<String>
        get() = _category

    private val _bodyPart = MutableLiveData("")
    val bodyPart: LiveData<String>
        get() = _bodyPart

    init {
        viewModelScope.launch {
            try {
                repo.getTemplateExercises().collect {
                    if (it.isNotEmpty()) exercises = it
                }
            } catch (e: CriticalDataNullException) {
                serviceError.initiateCountdown()
            }
        }
    }

    fun addExercise(exerciseTitle: String?) {
        if (exerciseTitle.isNullOrEmpty() || _category.value.isNullOrEmpty() || _bodyPart.value.isNullOrEmpty()) {
            _state.postValue(State.Added( message = "Please do not leave empty fields!"))
            return
        }

        if (exercises.any { it.name.equals(exerciseTitle, false) }) {
            _state.postValue(State.Added( message = "Names of template exercises must be unique!"))
            return
        }

        viewModelScope.launch {
            repo.addTemplateExercise(
                Exercise(
                    exerciseTitle,
                    BodyPart.entries.firstOrNull { it.key == _bodyPart.value!! }!!,
                    Category.entries.firstOrNull { it.key.equals(_category.value!!, true) }!!,
                    true,
                )
            )
            _state.postValue(State.Added(R.id.add_exercise_to_exercises, "Successfully added an exercise"))
        }
    }

    fun setBodyPart(info: String) {
        _bodyPart.value = BodyPart.entries.firstOrNull { it.key == info }.toString()
    }

    fun setCategory(info: String) {
        _category.value = Category.entries.firstOrNull { it.key == info }.toString()
    }

    sealed interface State {
        object Default : State
        data class Added(val action: Int? = null, val message: String) : State
    }
}
