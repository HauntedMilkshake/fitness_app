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
            _state.postValue(State.Added(false, "Please do not leave empty fields!"))
            return
        }

        if (exercises.any { it.name.equals(exerciseTitle, false) }) {
            _state.postValue(State.Added(false, "Names of template exercises must be unique!"))
        }

        viewModelScope.launch {
            repo.addTemplateExercise(
                Exercise(
                    exerciseTitle,
                    BodyPart.fromKey(_bodyPart.value!!)!!,
                    Category.fromKey(_category.value!!)!!,
                    true,
                )
            )
            _state.postValue(State.Added(true, "Successfully added an exercise"))
        }
    }

    fun setBodyPart(info: String) {
        _bodyPart.value = BodyPart.fromKey(info)?.toString()
    }

    fun setCategory(info: String) {
        _category.value = Category.fromKey(info)?.toString()
    }

    sealed interface State {
        object Default : State
        data class Added(val isAdded: Boolean, val message: String) : State
    }
}
