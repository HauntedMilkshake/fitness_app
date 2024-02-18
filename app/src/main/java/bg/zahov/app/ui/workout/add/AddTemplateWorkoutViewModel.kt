package bg.zahov.app.ui.workout.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ExerciseWithNoteVisibility
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getReplaceableExerciseProvider
import bg.zahov.app.getSelectableExerciseProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.hashString
import kotlinx.coroutines.launch

class AddTemplateWorkoutViewModel(application: Application) : AndroidViewModel(application) {
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

    private val _currExercises = MutableLiveData<List<ExerciseWithNoteVisibility>>()
    val currExercises: LiveData<List<ExerciseWithNoteVisibility>>
        get() = _currExercises

    var workoutNote: String = ""
    var workoutName: String = ""

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
                    _currExercises.postValue(it.map { selectable ->
                        ExerciseWithNoteVisibility(
                            selectable.exercise
                        )
                    })
                }
            }

            launch {
                replaceableExerciseProvider.exerciseToReplace.collect {
                    it?.let { replaced ->
                        if ((_currExercises.value?.get(exerciseToReplaceIndex)
                                ?: replaced.exercise) != replaced.exercise
                        ) {
                            val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
                            captured[exerciseToReplaceIndex] =
                                ExerciseWithNoteVisibility(replaced.exercise)
                            _currExercises.value = captured
                        }
                    }
                }
            }
        }
    }

    fun resetSelectedExercises() {
        selectableExerciseProvider.resetSelectedExercises()
    }

    fun setReplaceableExercise(item: ExerciseWithNoteVisibility) {
        exerciseToReplaceIndex = _currExercises.value?.indexOf(item) ?: -1
    }

    fun addWorkout() {
        if (workoutName.isEmpty()) {
            _state.value = State.Error("Cannot create a workout template without a name!")
            return
        }

        if (templates.map { it.name }.contains(workoutName)) {
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
                        id = hashString(workoutName),
                        name = workoutName,
                        duration = 0.0,
                        date = currDateToString(),
                        isTemplate = true,
                        exercises = _currExercises.value!!.map { it.exercise },
                    )
                )

                workoutName = ""
                _currExercises.postValue(listOf())

                _state.postValue(State.Default)
            }
        }
    }

    fun toggleExerciseNoteField(item: ExerciseWithNoteVisibility) {
        val captured = _currExercises.value ?: listOf()
        captured.find { it == item }?.noteVisibility = !item.noteVisibility

        _currExercises.value = captured
    }

//    fun onReplaceableExerciseClicked(newExercise: SelectableExercise) {
//        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
////        captured.find { it }
////        captured.remove( captured.find { it } )
//        _currExercises.value = captured
//    }

    fun removeExercise(item: ExerciseWithNoteVisibility) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        selectableExerciseProvider.removeExercise(SelectableExercise(item.exercise, true))
        captured.remove(item)
        _currExercises.value = captured
    }

    fun addSet(item: ExerciseWithNoteVisibility, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == item }
        foundExercise?.let {
            val newSets = it.exercise.sets.toMutableList()
            newSets.add(set)
            it.exercise.sets = newSets
        }
        _currExercises.value = exercises
    }

    fun removeSet(item: ExerciseWithNoteVisibility, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == item }
        foundExercise?.let {
            if (it.exercise.sets.isEmpty()) {
                val newSets = it.exercise.sets.toMutableList()
                newSets.remove(set)
                it.exercise.sets = newSets
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