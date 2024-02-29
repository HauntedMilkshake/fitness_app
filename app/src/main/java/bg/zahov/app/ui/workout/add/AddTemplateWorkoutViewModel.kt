package bg.zahov.app.ui.workout.add

import android.app.Application
import android.media.AudioTrack.OnPlaybackPositionUpdateListener
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.InteractableExerciseWrapper
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getReplaceableExerciseProvider
import bg.zahov.app.getSelectableExerciseProvider
import bg.zahov.app.getSettingsProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.generateRandomId
import bg.zahov.app.util.toExercise
import bg.zahov.app.util.toExerciseSetAdapterSetWrapper
import bg.zahov.app.util.toExerciseSetAdapterWrapper
import bg.zahov.app.util.toInteractableExerciseWrapper
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime

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

    private val settingsProvider by lazy {
        application.getSettingsProvider()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _currExercises = MutableLiveData<List<WorkoutEntry>>()
    val currExercises: LiveData<List<WorkoutEntry>>
        get() = _currExercises

    var workoutNote: String = ""
    var workoutName: String = ""
    private var exerciseToReplaceIndex: Int? = null
    private lateinit var templates: List<Workout>
    private var edit = false
    private lateinit var workoutIdToEdit: String
    private lateinit var settings: bg.zahov.app.data.local.Settings

    init {
        viewModelScope.launch {
            launch {
                try {
                    settingsProvider.getSettings().collect {
                        it.obj?.let { settingz ->
                            settings = settingz
                        }
                    }
                } catch (e: Exception) {
                    //TODO(Handle + get the appropriate exception)
                }
            }
            launch {
                try {
                    repo.getTemplateWorkouts().collect {
                        templates = it
                    }
                } catch (e: CriticalDataNullException) {
                    //todo()
                }
            }

            launch {
                selectableExerciseProvider.selectedExercises.collect {
                    if (it.isNotEmpty()) {
                        val captured = _currExercises.value.orEmpty().toMutableList()
                        captured.addAll(createWorkoutEntryArray(it))
                        _currExercises.postValue(captured)
                    }
                }
            }

            launch {
                replaceableExerciseProvider.exerciseToReplace.collect {
                    it?.let { replaced ->
                        val replacedEntry = ExerciseEntry(
                            replaced.toExerciseSetAdapterWrapper(
                                Units.valueOf(settings.units)
                            )
                        ),
                        exerciseToReplaceIndex?.let { indexToReplace ->
                            if (_currExercises.value?.get(indexToReplace) != replacedEntry) {
                                val captured =
                                    _currExercises.value?.toMutableList() ?: mutableListOf()
                                captured[indexToReplace] = replacedEntry
                                _currExercises.postValue(captured)
                                replaceableExerciseProvider.resetExerciseToReplace()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createWorkoutEntryArray(exercises: List<Exercise>): List<WorkoutEntry> {
        val workoutEntries = mutableListOf<WorkoutEntry>()
        exercises.forEach {
            workoutEntries.add(ExerciseEntry(it.toExerciseSetAdapterWrapper(Units.valueOf(settings.units))))
            it.sets.forEachIndexed { index, set ->
                workoutEntries.add(
                    SetEntry(
                        set.toExerciseSetAdapterSetWrapper(
                            index.toString(),
                            it.category,
                            "${(set.secondMetric)} x ${set.firstMetric}"
                        )
                    )
                )
            }
        }
        return workoutEntries
    }

    fun initEditWorkoutId(editFlag: Boolean, workoutId: String) {
        edit = editFlag
        workoutIdToEdit = workoutId
        if (workoutIdToEdit.isNotEmpty()) {
            viewModelScope.launch {
                repo.getTemplateWorkouts()
                    .filter { it.find { workout -> workout.id == workoutId } != null }.collect {
                        it.first().note?.let { note -> workoutNote = note }
                        workoutName = it.first().name
                        _currExercises.postValue(createWorkoutEntryArray(it.first().exercises))
                    }
            }
        }
    }

    fun resetSelectedExercises() {
        selectableExerciseProvider.resetSelectedExercises()
    }

    fun setReplaceableExercise(itemPosition: Int) {
        exerciseToReplaceIndex = itemPosition
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
                val exercises = toNormalExercises(_currExercises.value!!)
                var volume = 0.0
                exercises.forEach {
                    if(it.category != Category.Cardio && it.category != Category.Timed && it.category != Category.RepsOnly) {
                        it.sets.forEach {set ->
                            volume += ((set.firstMetric ?: 1) * (set.secondMetric ?: 1)).toDouble()
                        }
                    }
                }
                repo.addTemplateWorkout(
                    Workout(
                        id = if (edit) workoutIdToEdit else generateRandomId(),
                        name = workoutName,
                        note = workoutNote,
                        duration = 0L,
                        date = LocalDateTime.now(),
                        isTemplate = true,
                        exercises = exercises,
                        volume = null,
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
    private fun toNormalExercises(entries: List<WorkoutEntry>): List<Exercise> {
        val exercises = mutableListOf<Exercise>()
        var currentExercisesIndex: Int = 0
        entries.forEachIndexed { index, entry ->
            when(entry) {
                is ExerciseEntry -> {
                    currentExercisesIndex = index
                    exercises.add(entry.exerciseEntry.toExercise())
                }
                is SetEntry -> {
                    exercises[currentExercisesIndex].sets.add(entry.setEntry.set)
                }
            }
        }
        return exercises
    }
    private fun getExerciseFromPosition(entries: List<WorkoutEntry>, position: Int): Exercise? {
        var exercise: Exercise? = null
        for(entry in position until entries.size) {
            when(entries[entry]) {
                is ExerciseEntry -> {
                    exercise = (entries[entry] as ExerciseEntry).exerciseEntry.toExercise()
                }
                is SetEntry -> {
                    exercise?.sets?.add((entries[entry] as SetEntry).setEntry.set)
                }
            }
        }
        return exercise
    }

    fun toggleExerciseNoteField(position: Int) {
        val captured = _currExercises.value ?: listOf()
        (captured[position] as? ExerciseEntry)?.exerciseEntry?.note = " "
        _currExercises.value = captured
    }

    fun removeExercise(position: Int) {
        val captured = _currExercises.value.orEmpty().toMutableList()

        getExerciseFromPosition(captured, position)?.let {
            selectableExerciseProvider.removeExercise(it)
        }

        captured.removeAt(position)
        _currExercises.value = captured
    }

    fun addSet(position: Int) {
        val exercises = _currExercises.value.orEmpty().toMutableList()
        val exercise = getExerciseFromPosition(exercises, position)
        exercise?.let {
            exercises.add(it.sets.size + 1, SetEntry(Sets(SetType.DEFAULT, null, null).toExerciseSetAdapterSetWrapper((it.sets.size + 1).toString(), it.category, )))
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