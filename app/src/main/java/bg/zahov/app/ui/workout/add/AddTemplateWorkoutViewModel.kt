package bg.zahov.app.ui.workout.add

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getReplaceableExerciseProvider
import bg.zahov.app.getSelectableExerciseProvider
import bg.zahov.app.getSettingsProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.generateRandomId
import bg.zahov.app.util.toExercise
import bg.zahov.app.util.toExerciseSetAdapterSetWrapper
import bg.zahov.app.util.toExerciseSetAdapterWrapper
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime

class AddTemplateWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
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
    private var templateExercises = listOf<Exercise>()

    init {
        viewModelScope.launch {
            launch {
                try {
                    workoutProvider.getTemplateExercises().collect {
                        templateExercises = it
                    }
                } catch (e: CriticalDataNullException) {
                    //TODO()
                }
            }
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
                    workoutProvider.getTemplateWorkouts().collect {
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
                        )
                        exerciseToReplaceIndex?.let { indexToReplace ->
                            if (_currExercises.value?.get(indexToReplace) != replacedEntry) {
                                val captured =
                                    _currExercises.value.orEmpty().toMutableList()
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
                workoutProvider.getTemplateWorkouts()
                    .filter { it.find { workout -> workout.id == workoutId } != null }.collect {
                        it.first().note?.let { note -> workoutNote = note }
                        workoutName = it.first().name
                        _currExercises.postValue(createWorkoutEntryArray(it.first().exercises))
                    }
            }
        }
    }

    fun onSetCheckClicked(itemPosition: Int) {
        val captured = _currExercises.value.orEmpty()
        (captured[itemPosition] as? SetEntry)?.setEntry?.backgroundResource =
            if ((captured[itemPosition] as? SetEntry)?.setEntry?.backgroundResource == R.color.background) R.color.completed_set else R.color.background
        _currExercises.value = captured
    }

    fun resetSelectedExercises() {
        selectableExerciseProvider.resetSelectedExercises()
    }

    fun setReplaceableExercise(itemPosition: Int) {
        exerciseToReplaceIndex = itemPosition
    }

    fun saveTemplateWorkout() {
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
                val exercises = getNormalExercises(_currExercises.value!!)
                var volume = 0.0
                exercises.forEach {
                    if (it.category != Category.Cardio && it.category != Category.Timed && it.category != Category.RepsOnly) {
                        it.sets.forEach { set ->
                            volume += ((set.firstMetric ?: 1).toDouble() * (set.secondMetric
                                ?: 1).toDouble())
                        }
                    }
                }
                workoutProvider.addTemplateWorkout(
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

    private fun getNormalExercises(entries: List<WorkoutEntry>): List<Exercise> {
        val exercises = mutableListOf<Exercise>()
        var currentExercisesIndex = 0
        entries.forEachIndexed { index, entry ->
            when (entry) {
                is ExerciseEntry -> {
                    if (index != 0) currentExercisesIndex++
                    exercises.add(entry.exerciseEntry.toExercise())
                }

                is SetEntry -> {
                    exercises[currentExercisesIndex].sets.add(entry.setEntry.set)
                }
            }
        }
        return exercises
    }

    fun toggleExerciseNoteField(position: Int) {
        val captured = _currExercises.value.orEmpty()
        (captured[position] as? ExerciseEntry)?.exerciseEntry?.noteVisibility =
            if ((captured[position] as? ExerciseEntry)?.exerciseEntry?.noteVisibility == View.VISIBLE) View.GONE else View.VISIBLE
        _currExercises.value = captured
    }

    fun removeExercise(position: Int) {
        val captured = _currExercises.value.orEmpty().toMutableList()
        captured.removeAt(position)
        while (position < captured.size && captured[position] is SetEntry) {
            captured.removeAt(position)
        }
        _currExercises.value = captured
    }

    fun addSet(position: Int) {
        var edgeCaseFlag = false
        val exercises = _currExercises.value.orEmpty().toMutableList()
        val templateExercise =
            templateExercises.find { it.name == (exercises[position] as? ExerciseEntry)?.exerciseEntry?.name }

        if (exercises.size == 1 || position == exercises.size - 1) {
            insertSetAtIndex(exercises, position + 1, position, templateExercise)
            edgeCaseFlag = true
        }

        if (!edgeCaseFlag) {
            var index = position + 1

            while (index < exercises.size && exercises[index] !is ExerciseEntry) {
                index++
            }

            insertSetAtIndex(exercises, index, position, templateExercise)
        }

        _currExercises.value = exercises
    }

    private fun insertSetAtIndex(
        exercises: MutableList<WorkoutEntry>,
        insertIndex: Int,
        exercisePosition: Int,
        templateExercise: Exercise?,
    ) {
        val setNumber = insertIndex - exercisePosition
        val setEntry = if (templateExercise != null && setNumber <= templateExercise.sets.size) {
            SetEntry(
                templateExercise.sets[setNumber].toExerciseSetAdapterSetWrapper(
                    setNumber.toString(),
                    templateExercise.category,
                    "${templateExercise.sets[setNumber].secondMetric} x  ${templateExercise.sets[setNumber].secondMetric}"
                )
            )
        } else {
            SetEntry(
                ExerciseSetAdapterSetWrapper(
                    secondInputFieldVisibility = when (templateExercise?.category) {
                        Category.RepsOnly, Category.Cardio, Category.Timed -> View.GONE
                        else -> View.VISIBLE
                    },
                    setNumber = setNumber.toString(),
                    previousResults = "-/-",
                    set = Sets(SetType.DEFAULT, 0.0, 0)
                )
            )
        }
        exercises.add(insertIndex, setEntry)
    }

    fun removeSet(position: Int) {
        val exercises = _currExercises.value.orEmpty().toMutableList()
        exercises.removeAt(position)
        for (index in position until exercises.size) {
            if (exercises[index] is SetEntry) {
                (exercises[index] as SetEntry).setEntry.setNumber =
                    ((exercises[index] as SetEntry).setEntry.setNumber.toInt() - 1).toString()
            } else {
                break
            }
        }
        _currExercises.value = exercises
    }

    fun onInputFieldChanged(
        position: Int,
        metric: String,
        viewId: Int,
    ) {
        if (position != -1 && position < (_currExercises.value?.size ?: -1)) {
            when (viewId) {
                R.id.first_input_field_text -> {
                    (_currExercises.value?.get(position) as? SetEntry)?.setEntry?.set?.firstMetric =
                        metric.toDoubleOrNull()
                }

                R.id.second_input_field_text -> {
                    (_currExercises.value?.get(position) as? SetEntry)?.setEntry?.set?.secondMetric =
                        metric.toIntOrNull()
                }
            }
        }
    }

    fun onSetTypeChanged(itemPosition: Int, setType: SetType) {
        val captured = _currExercises.value.orEmpty()
        (captured[itemPosition] as? SetEntry)?.setEntry?.set?.type = setType
        _currExercises.value = captured
    }

    fun changeNote(itemPosition: Int, text: String) {
        (_currExercises.value?.get(itemPosition) as? ExerciseEntry)?.exerciseEntry?.note = text
    }

    sealed interface State {
        object Default : State
        data class Error(val eMessage: String) : State
        data class Success(val nMessage: String) : State
    }
}