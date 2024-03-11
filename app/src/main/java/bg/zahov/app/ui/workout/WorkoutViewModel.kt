package bg.zahov.app.ui.workout

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.RestState
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.getAddExerciseToWorkoutProvider
import bg.zahov.app.getRestTimerProvider
import bg.zahov.app.getSettingsProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.ui.workout.add.ExerciseEntry
import bg.zahov.app.ui.workout.add.ExerciseSetAdapterSetWrapper
import bg.zahov.app.ui.workout.add.SetEntry
import bg.zahov.app.ui.workout.add.WorkoutEntry
import bg.zahov.app.util.hashString
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.app.util.toExerciseSetAdapterSetWrapper
import bg.zahov.app.util.toExerciseSetAdapterWrapper
import bg.zahov.fitness.app.R
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Random

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutStateManager by lazy {
        application.getWorkoutStateManager()
    }

    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val addExerciseToWorkoutProvider by lazy {
        application.getAddExerciseToWorkoutProvider()
    }

    private val restTimerProvider by lazy {
        application.getRestTimerProvider()
    }

    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }

    private val settingsProvider by lazy {
        application.getSettingsProvider()
    }

    private val _exercises = MutableLiveData<List<WorkoutEntry>>()
    val exercises: LiveData<List<WorkoutEntry>>
        get() = _exercises

    private val _name = MutableLiveData("New workout")
    val name: LiveData<String>
        get() = _name

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    private val _restTimerState = MutableLiveData<State>(State.Default(false))
    val restTimer: LiveData<State>
        get() = _restTimerState

    private val _note = MutableLiveData<String>()
    val note: LiveData<String>
        get() = _note

    private var exerciseToReplaceIndex: Int? = null
    private var templateExercises = listOf<Exercise>()
    private lateinit var units: Units
    private var workoutId: String? = null

    init {
        viewModelScope.launch {
            launch {
                settingsProvider.getSettings().collect { objectChange ->
                    objectChange.obj?.units?.let {
                        units = Units.valueOf(it)
                    }
                }
            }
            launch {
                workoutStateManager.template.collect {
                    it?.let { workout ->
                        val exercisesToAdd = createWorkoutEntryArray(workout.exercises)
                        val exercises = _exercises.value.orEmpty().toMutableList()
                        if (exercisesToAdd.isNotEmpty()) {
                            exercises.addAll(exercisesToAdd)
                            _exercises.postValue(exercises)
                        }
                        _name.postValue(workout.name)
                        _note.postValue(workout.note ?: "")
                        workoutId = workout.id
                    }
                }
            }
            launch {
                workoutStateManager.timer.collect {
                    _timer.postValue(
                        String.format(
                            "%02d:%02d:%02d",
                            (it / (1000 * 60 * 60)) % 24,
                            (it / (1000 * 60)) % 60,
                            (it / 1000) % 60
                        )
                    )
                }
            }
            launch {
                addExerciseToWorkoutProvider.selectedExercises.collect {
                    if (it.isNotEmpty()) {
                        val exercisesToUpdate = _exercises.value.orEmpty().toMutableList()
                        exercisesToUpdate.addAll(createWorkoutEntryArray(it))
                        _exercises.postValue(exercisesToUpdate)
                    }
                }
            }
            launch {
                restTimerProvider.restTimer.collect {
                    it.elapsedTime?.let { time ->
                        _restTimerState.postValue(State.Rest(time))
                    }
                }
            }
            launch {
                restTimerProvider.restState.collect {
                    _restTimerState.postValue(
                        when (it) {
                            RestState.Active -> State.Default(true)
                            else -> State.Default(false)
                        }
                    )
                }
            }
            launch {
                workoutProvider.getTemplateExercises().collect {
                    templateExercises = it
                }
            }
        }
    }

    private fun createWorkoutEntryArray(exercises: List<Exercise>): List<WorkoutEntry> {
        val workoutEntries = mutableListOf<WorkoutEntry>()
        exercises.forEach {
            workoutEntries.add(ExerciseEntry(it.toExerciseSetAdapterWrapper(if (::units.isInitialized) units else Units.METRIC)))
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

    fun onExerciseReplace(itemPosition: Int) {
        exerciseToReplaceIndex = itemPosition
    }

    fun onSetCheckClicked(itemPosition: Int) {
        val captured = _exercises.value.orEmpty()
        (captured[itemPosition] as? SetEntry)?.setEntry?.backgroundResource =
            if ((captured[itemPosition] as? SetEntry)?.setEntry?.backgroundResource == R.color.background) R.color.completed_set else R.color.background
        _exercises.value = captured
    }

    fun toggleExerciseNoteField(position: Int) {
        val captured = _exercises.value.orEmpty()
        (captured[position] as? ExerciseEntry)?.exerciseEntry?.noteVisibility =
            if ((captured[position] as? ExerciseEntry)?.exerciseEntry?.noteVisibility == View.GONE) View.VISIBLE else View.GONE
        _exercises.value = captured
    }

    fun removeExercise(position: Int) {
        val captured = _exercises.value.orEmpty().toMutableList()
        captured.removeAt(position)
        while (position < captured.size && captured[position] is SetEntry) {
            captured.removeAt(position)
        }
        _exercises.value = captured
    }

    fun addSet(position: Int) {
        var edgeCaseFlag = false
        val exercises = _exercises.value.orEmpty().toMutableList()
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

        _exercises.value = exercises
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
        val exercises = _exercises.value.orEmpty().toMutableList()
        exercises.removeAt(position)
        for (index in position until exercises.size) {
            if (exercises[index] is SetEntry) {
                (exercises[index] as SetEntry).setEntry.setNumber =
                    ((exercises[index] as SetEntry).setEntry.setNumber.toInt() - 1).toString()
            } else {
                break
            }
        }
        _exercises.value = exercises
    }

    fun onInputFieldChanged(
        position: Int,
        metric: String,
        viewId: Int,
    ) {
        if (position != -1 && position < (_exercises.value?.size ?: -1)) {
            when (viewId) {
                R.id.first_input_field_text -> {
                    (_exercises.value?.get(position) as? SetEntry)?.setEntry?.set?.firstMetric =
                        metric.toDoubleOrNull()
                }

                R.id.second_input_field_text -> {
                    (_exercises.value?.get(position) as? SetEntry)?.setEntry?.set?.secondMetric =
                        metric.toIntOrNull()
                }
            }
        }
    }

    fun onSetTypeChanged(itemPosition: Int, setType: SetType) {
        val captured = _exercises.value.orEmpty()
        (captured[itemPosition] as? SetEntry)?.setEntry?.set?.type = setType
        _exercises.value = captured
    }

    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.updateState(WorkoutState.MINIMIZED)
        }
    }

    fun cancel() {
        viewModelScope.launch {
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }

    fun finishWorkout() {
        if (_exercises.value.isNullOrEmpty()) {
            _restTimerState.postValue(State.Error("Cannot finish a workout without any exercises!"))
            return
        }

        viewModelScope.launch {
            val (exercises, prs, volume) = getExerciseArrayAndPRs(_exercises.value!!)
            repo.addWorkoutToHistory(
                Workout(
                    id = workoutId ?: hashString("${Random().nextInt(Int.MAX_VALUE)}"),
                    name = "${getTimePeriodAsString()} ${_name.value}",
                    date = LocalDateTime.now(),
                    exercises = exercises,
                    note = _note.value,
                    duration = _timer.value?.parseTimeStringToLong() ?: 0L,
                    isTemplate = false,
                    personalRecords = prs,
                    volume = volume
                )
            )
            addExerciseToWorkoutProvider.resetSelectedExercises()
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }

    private fun getExerciseArrayAndPRs(entries: List<WorkoutEntry>): Triple<List<Exercise>, Int, Double> {
        val exercises = linkedMapOf<String, Exercise>()
        var prs = 0
        var volume = 0.0
        entries.forEach { entry ->
            when (entry) {
                is ExerciseEntry -> {
                    exercises.getOrPut(entry.exerciseEntry.name) {
                        Exercise(
                            name = entry.exerciseEntry.name,
                            bodyPart = entry.exerciseEntry.bodyPart,
                            category = entry.exerciseEntry.category,
                            isTemplate = false,
                            note = entry.exerciseEntry.note
                        )
                    }
                }

                is SetEntry -> {
                    exercises.entries.last().value.apply {
                        sets.add(entry.setEntry.set)
                        if (entry.setEntry.set.type == SetType.DEFAULT || entry.setEntry.set.type == SetType.FAILURE) {
                            when (category) {
                                Category.RepsOnly -> {
                                    if ((entry.setEntry.set.secondMetric
                                            ?: 0) > (bestSet.secondMetric ?: 0)
                                    ) {
                                        bestSet = entry.setEntry.set
                                    }
                                }

                                else -> {
                                    if ((entry.setEntry.set.firstMetric
                                            ?: 0.0) > (bestSet.firstMetric ?: 0.0)
                                    ) {
                                        bestSet = entry.setEntry.set
                                    }
                                    if (category != Category.Cardio && category != Category.Timed && category != Category.AssistedWeight) {
                                        volume += entry.setEntry.set.firstMetric
                                            ?: (1.0 * (entry.setEntry.set.secondMetric
                                                ?: 1).toInt())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            val temp = exercises.values.toMutableList()
            temp.forEach { currExercise ->
                templateExercises.find { it.name == currExercise.name }?.let { template ->
                    when (currExercise.category) {
                        Category.RepsOnly -> {
                            if ((currExercise.bestSet.secondMetric
                                    ?: 0) <= (template.bestSet.secondMetric ?: 0)
                            ) {
                                currExercise.bestSet = template.bestSet
                            } else {
                                prs++
                            }
                        }

                        else -> {
                            if ((currExercise.bestSet.firstMetric
                                    ?: 0.0) <= (template.bestSet.firstMetric ?: 0.0)
                            ) {
                                currExercise.bestSet = template.bestSet
                            } else {
                                prs++
                            }
                        }
                    }
                    currExercise.isTemplate = true
                }
            }
            workoutProvider.updateExercises(exercises.values.toList())
        }
        return Triple(exercises.values.toList(), prs, volume)
    }

    private fun getTimePeriodAsString() = when (LocalTime.now().hour) {
        in 6..11 -> "Morning"
        in 12..16 -> "Noon"
        in 17..20 -> "Afternoon"
        else -> "Night"
    }

    fun changeNote(itemPosition: Int, text: String) {
        (_exercises.value?.get(itemPosition) as? ExerciseEntry)?.exerciseEntry?.note = text
    }

    sealed interface State {
        data class Default(val restState: Boolean) : State
        data class Rest(val time: String) : State
        data class Error(val message: String?) : State
    }
}