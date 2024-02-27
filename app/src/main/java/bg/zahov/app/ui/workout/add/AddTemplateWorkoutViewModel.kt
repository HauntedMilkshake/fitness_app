package bg.zahov.app.ui.workout.add

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.InteractableExerciseWrapper
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getReplaceableExerciseProvider
import bg.zahov.app.getSelectableExerciseProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.generateRandomId
import bg.zahov.app.util.toExercise
import bg.zahov.app.util.toInteractableExerciseWrapper
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    private val _currExercises = MutableLiveData<List<InteractableExerciseWrapper>>()
    val currExercises: LiveData<List<InteractableExerciseWrapper>>
        get() = _currExercises

    var workoutNote: String = ""
    var workoutName: String = ""
    private var exerciseToReplaceIndex: Int? = null
    private lateinit var templates: List<Workout>
    private var edit = false
    private lateinit var workoutIdToEdit: String

    init {
        viewModelScope.launch {
            launch {
                repo.getTemplateWorkouts().collect {
                    templates = it
                }
            }

            launch {
                selectableExerciseProvider.selectedExercises.collect {
                    if (it.isNotEmpty()) {
                        val captured = _currExercises.value.orEmpty().toMutableList()
                        captured.addAll(it)
                        _currExercises.postValue(captured)
                    }
                }
            }

            launch {
                replaceableExerciseProvider.exerciseToReplace.collect {
                    it?.let { replaced ->
                        exerciseToReplaceIndex?.let { indexToReplace ->
                            if (_currExercises.value?.get(indexToReplace) != replaced) {
                                val captured =
                                    _currExercises.value?.toMutableList() ?: mutableListOf()
                                captured[indexToReplace] = replaced
                                _currExercises.postValue(captured)
                                replaceableExerciseProvider.resetExerciseToReplace()
                            }
                        }
                    }
                }
            }
        }
    }

    fun initEditWorkoutId(editFlag: Boolean, workoutId: String) {
        edit = editFlag
        workoutIdToEdit = workoutId
        if (workoutIdToEdit.isNotEmpty()) {
            viewModelScope.launch {
                repo.getTemplateWorkouts()
                    .filter { it.find { workout -> workout.id == workoutId } != null }.collect {
                        Log.d("ITEMS + ID", "${it.size} ${it.first().id}")
                        it.first().note?.let { note -> workoutNote = note }
                        workoutName = it.first().name
                        _currExercises.postValue(it.first().exercises.map { exercise -> exercise.toInteractableExerciseWrapper() })
                    }
            }
        }
    }

    fun resetSelectedExercises() {
        selectableExerciseProvider.resetSelectedExercises()
    }

    fun setReplaceableExercise(item: InteractableExerciseWrapper) {
        exerciseToReplaceIndex = _currExercises.value?.indexOf(item)
    }

    fun onSave() {
        if (workoutName.isEmpty()) {
            _state.value = State.Error("Cannot create a workout template without a name!")
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
                        id = if (edit) workoutIdToEdit else generateRandomId(),
                        name = workoutName,
                        note = workoutNote,
                        duration = 0L,
                        date = LocalDate.now(),
                        isTemplate = true,
                        exercises = _currExercises.value!!.map { it.toExercise() },
                    )
                )
            }

            workoutName = ""
            workoutNote = ""
            _currExercises.postValue(listOf())
            resetSelectedExercises()

        }
        _state.value =
            State.Success(if (!edit) "Successfully added workout!" else "Successfully edited workout")
        _state.value = State.Default
    }

    fun toggleExerciseNoteField(position: Int) {
        val captured = _currExercises.value ?: listOf()
        captured[position].isNoteVisible = !captured[position].isNoteVisible
        _currExercises.value = captured
    }

//    fun onReplaceableExerciseClicked(newExercise: SelectableExercise) {
//        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
////        captured.find { it }
////        captured.remove( captured.find { it } )
//        _currExercises.value = captured
//    }

    fun removeExercise(item: InteractableExerciseWrapper) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        selectableExerciseProvider.removeExercise(item)
        captured.remove(item)
        _currExercises.value = captured
    }

    fun addSet(item: InteractableExerciseWrapper, set: ClickableSet) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == item }
        foundExercise?.let {
            val newSets = it.sets.toMutableList()
            newSets.add(set)
            it.sets = newSets
        }
        _currExercises.value = exercises
    }

    fun removeSet(item: InteractableExerciseWrapper, set: ClickableSet) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        exercises.find { it == item }?.let {
            if (it.sets.isNotEmpty()) {
                val newSets = it.sets.toMutableList()
                newSets.remove(set)
                it.sets = newSets
            }
        }
        _currExercises.value = exercises
    }

    fun onInputFieldTextChanged(
        exercise: InteractableExerciseWrapper,
        set: ClickableSet,
        metric: String,
        viewId: Int,
    ) {
        val new = currExercises.value?.find { it == exercise }
        when (viewId) {
            R.id.first_input_field_text -> {
                new?.sets?.find { it == set }?.set?.firstMetric = metric.toDoubleOrNull()
            }

            R.id.second_input_field_text -> {
                new?.sets?.find { it == set }?.set?.secondMetric = metric.toIntOrNull()
            }
        }
    }

    sealed interface State {
        object Default : State
        data class Error(val eMessage: String) : State
        data class Success(val nMessage: String) : State
    }
}