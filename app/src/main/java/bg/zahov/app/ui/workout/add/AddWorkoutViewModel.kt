package bg.zahov.app.ui.workout.add

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.toExerciseList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _currExercises = MutableLiveData<List<Exercise>>()
    val currExercises: LiveData<List<Exercise>>
        get() = _currExercises

    lateinit var templates: List<Workout>
    init {
        viewModelScope.launch {
            repo.getTemplateWorkouts().collect {
                templates = it
            }
        }
    }

    fun addWorkout(name: String) {
        if(name.isEmpty()) {
            _state.value = State.Error("Cannot create a workout template without a name!")
        }

        if(templates.map { it.name }.contains(name)) {
            _state.value = State.Error("Each workout must have a unique name!")
        }
        _currExercises.value?.let {exercises ->
            if (exercises .isEmpty()) {
                _state.value = State.Error("Each workout must have a unique name!")
                return
            } else {
                viewModelScope.launch {
                    repo.addWorkout(
                        Workout(
                            name = name,
                            duration = 0.0,
                            date = LocalDate.now()
                                .format(
                                    DateTimeFormatter.ofPattern(
                                        "dd-MM-yyyy",
                                        Locale.getDefault()
                                    )
                                ),
                            isTemplate = true,
                            exercises = emptyList(),
                            ids = exercises.map{ it.name }
                        )
                    )

                    _state.postValue(State.Success("Template workout $name successfully created!"))
                }
            }
        }
    }

//    fun addSelectedExercises(selectedExercises: List<SelectableExercise>) {
//        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
//        captured.addAll(selectedExercises.toExerciseList())
//        _currExercises.value = captured
//    }

    fun addExercise(newExercise: Exercise) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        captured.add(newExercise)
        _currExercises.value = captured
        captured.forEach {
            Log.d("ADD", it.name)
        }
    }

    fun removeExercise(item: Exercise) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        captured.remove(item)
        _currExercises.value = captured
        captured.forEach {
            Log.d("DELETE", it.name)
        }
    }

    fun addSet(exercise: Exercise, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == exercise }
        Log.d("ADDING SET", "OLD SET SIZE ${foundExercise?.sets?.size}")
        foundExercise?.let {
            val newSets = it.sets.toMutableList()
            newSets.add(set)
            it.sets = newSets
            Log.d("ADDING SET", "NEW SET SIZE ${foundExercise.sets.size}")
        }
        _currExercises.value = exercises
    }

    fun removeSet(exercise: Exercise, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == exercise }
        Log.d("REMOVING SET", "OLD SET SIZE ${foundExercise?.sets?.size}")
        foundExercise?.let {
            if (it.sets.isEmpty()) {
                val newSets = it.sets.toMutableList()
                newSets.remove(set)
                Log.d("REMOVING SET", "NEW SET SIZE ${foundExercise.sets.size}")
                it.sets = newSets
            }
        }

        _currExercises.value = exercises
    }

    fun resetSelectedExercises() {
        _currExercises.value = listOf()
    }

    sealed interface State {
        data class Error(val eMessage: String) : State
        data class Success(val nMessage: String) : State
    }
}