package bg.zahov.app.ui.workout.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getReplaceableExerciseProvider
import bg.zahov.app.getSelectableExerciseProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.hashString
import kotlinx.coroutines.launch

class AddWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val selectableExerciseProvider by lazy {
        application.getSelectableExerciseProvider()
    }

    private val replaceableExerciseProvider by lazy {
        application.getReplaceableExerciseProvider()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _currExercises = MutableLiveData<List<Exercise>>()
    val currExercises: LiveData<List<Exercise>>
        get() = _currExercises

    private val _workoutName = MutableLiveData<String>()

    val workoutName: LiveData<String>
        get() = _workoutName

    private var exerciseToReplaceIndex: Int = -1

    private lateinit var templates: List<Workout>

    init {
        viewModelScope.launch {
            launch {
                repo.getTemplateWorkouts().collect {
                    templates = it
                }
            }

            launch {
                selectableExerciseProvider.selectedExercises.collect {
                    _currExercises.postValue( it.map {selectable -> selectable.exercise })
                }
            }

            launch {
                replaceableExerciseProvider.exerciseToReplace.collect {
                    it?.let { replaced ->
                        if((_currExercises.value?.get(exerciseToReplaceIndex)
                                ?: replaced.exercise) != replaced.exercise
                        ) {
                            val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
                            captured[exerciseToReplaceIndex] = replaced.exercise
                            _currExercises.value = captured
                        }
                    }
                }
            }
        }
    }

    fun setReplaceableExercise(item: Exercise) {
        exerciseToReplaceIndex = _currExercises.value?.indexOf(item) ?: -1
    }

    fun setWorkoutName(name: String) {
        _workoutName.value = name
    }

    fun addWorkout() {
        if (_workoutName.value.isNullOrEmpty()) {
            _state.value = State.Error("Cannot create a workout template without a name!")
            return
        }

        if (templates.map { it.name }.contains(_workoutName.value)) {
            _state.value = State.Error("Each workout must have a unique name!")
            return
        }

        _currExercises.value?.let { exercises ->
            if (exercises.isEmpty()) {
                _state.value = State.Error("Cannot create a workout without exercises!")
                return
            }

            viewModelScope.launch {
                repo.addTemplateWorkout(
                    Workout(
                        id = hashString(_workoutName.value!!),
                        name = _workoutName.value!!,
                        duration = 0.0,
                        date = currDateToString(),
                        isTemplate = true,
                        exercises = _currExercises.value!!,
                    )
                )

                _workoutName.postValue("")
                _currExercises.postValue(listOf())

                _state.postValue(State.Default)
            }
        }
    }

//    fun onReplaceableExerciseClicked(newExercise: SelectableExercise) {
//        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
////        captured.find { it }
////        captured.remove( captured.find { it } )
//        _currExercises.value = captured
//    }

    fun removeExercise(exercise: Exercise) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        captured.remove(exercise)
        _currExercises.value = captured
    }

    fun addSet(exercise: Exercise, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == exercise }
        foundExercise?.let {
            val newSets = it.sets.toMutableList()
            newSets.add(set)
            it.sets = newSets
        }
        _currExercises.value = exercises
    }

    fun removeSet(exercise: Exercise, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == exercise }
        foundExercise?.let {
            if (it.sets.isEmpty()) {
                val newSets = it.sets.toMutableList()
                newSets.remove(set)
                it.sets = newSets
            }
        }

        _currExercises.value = exercises
    }

//    fun resetSelectedExercises() {
//        selectableExerciseProvider.resetSelectedExercises()
//    }

    sealed interface State {
        object Default : State
        data class Error(val eMessage: String) : State
        data class Success(val nMessage: String) : State
    }
}