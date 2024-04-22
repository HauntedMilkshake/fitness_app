package bg.zahov.app.ui.workout

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.RestState
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getAddExerciseToWorkoutProvider
import bg.zahov.app.getRestTimerProvider
import bg.zahov.app.getSettingsProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.ui.workout.add.ExerciseEntry
import bg.zahov.app.ui.workout.add.ExerciseSetAdapterSetWrapper
import bg.zahov.app.ui.workout.add.SetEntry
import bg.zahov.app.ui.workout.add.WorkoutEntry
import bg.zahov.app.util.filterIntegerInput
import bg.zahov.app.util.hashString
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.app.util.toExerciseSetAdapterSetWrapper
import bg.zahov.app.util.toExerciseSetAdapterWrapper
import bg.zahov.app.util.toRealmExercise
import bg.zahov.app.util.toRealmString
import bg.zahov.fitness.app.R
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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
    private val _navigate = MutableLiveData<Int?>()
    val navigate: LiveData<Int?>
        get() = _navigate
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
    private val _notify = MutableLiveData<String?>()
    val notify: LiveData<String?>
        get() = _notify

    private var exerciseToReplaceIndex: Int? = null
    private var templateExercises = listOf<Exercise>()
    private lateinit var units: Units
    private var workoutId: String? = null
    private val workoutDate: LocalDateTime = LocalDateTime.now()

    init {
        viewModelScope.launch {
            launch {
                workoutStateManager.shouldSave.collect {
                    if (it) {
                        saveWorkoutState()
                    }
                }
            }
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
                        val exercisesToAdd =
                            createWorkoutEntryArray(workout.exercises, workoutStateManager.resuming)
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
                        exercisesToUpdate.addAll(createWorkoutEntryArray(it, false))
                        _exercises.postValue(exercisesToUpdate)
                        addExerciseToWorkoutProvider.resetSelectedExercises()
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

    private fun createWorkoutEntryArray(
        exercises: List<Exercise>,
        resuming: Boolean,
    ): List<WorkoutEntry> {
        val workoutEntries = mutableListOf<WorkoutEntry>()
        exercises.forEach {
            workoutEntries.add(ExerciseEntry(it.toExerciseSetAdapterWrapper(if (::units.isInitialized) units else Units.METRIC)))
            it.sets.forEachIndexed { index, set ->
                workoutEntries.add(
                    SetEntry(
                        set.toExerciseSetAdapterSetWrapper(
                            (index + 1).toString(),
                            it.category,
                            previousResults = "${(set.secondMetric)} x ${set.firstMetric}",
                            resumeSet = if (resuming) set else null
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
        Log.d("add set", "inital list $exercises")
        val templateExercise =
            templateExercises.find { it.name == (exercises[position] as? ExerciseEntry)?.exerciseEntry?.name }
        Log.d("add set", "template exercise ${if(templateExercise == null) "bad" else "good"}")
        if (exercises.size == 1 || position == exercises.size - 1) {
            Log.d("add set", "adding first set")
            insertSetAtIndex(exercises, position + 1, position, templateExercise)
            edgeCaseFlag = true
        }

        if (!edgeCaseFlag) {
            Log.d("add set", "adding not first set")
            var index = position + 1
            while (index < exercises.size && exercises[index] !is ExerciseEntry) {
                index++
            }

            Log.d("add set", "insert index:$index exercise position:$position")
            insertSetAtIndex(exercises, index, position, templateExercise)
        }

        _exercises.value = exercises
    }

    //likely a bug somewhere here
    private fun insertSetAtIndex(
        exercises: MutableList<WorkoutEntry>,
        insertIndex: Int,
        exercisePosition: Int,
        templateExercise: Exercise?,
    ) {
        val setNumber = insertIndex - exercisePosition
        val setEntry = if (templateExercise != null && setNumber < templateExercise.sets.size) {
            SetEntry(
                templateExercise.sets[setNumber].toExerciseSetAdapterSetWrapper(
                    setNumber.toString(),
                    templateExercise.category,
                    "${templateExercise.sets[setNumber].secondMetric} x ${templateExercise.sets[setNumber].secondMetric}"
                )
            )
        } else {
            SetEntry(
                ExerciseSetAdapterSetWrapper(
                    secondInputFieldVisibility = when (templateExercise?.category) {
                        Category.RepsOnly, Category.Cardio, Category.Timed -> View.GONE
                        else -> View.VISIBLE
                    },
                    setNumber = if (setNumber == 0) 1.toString() else setNumber.toString(),
                    previousResults = "-/-",
                    set = Sets(SetType.DEFAULT, 0.0, 0)
                )
            )
        }
        Log.d("add set", "added set: ${setEntry.setEntry.set}  additional info ${setEntry.setEntry.previousResults}")
        exercises.add(insertIndex, setEntry)
    }
    //bug when removing set gets moved downward
    fun removeSet(position: Int) {
        val exercises = _exercises.value.orEmpty().toMutableList()
        exercises.removeAt(position)
        var index = position
        while (index < exercises.size && exercises[index] is SetEntry) {
            (exercises[index] as SetEntry).setEntry.setNumber =
                ((exercises[index] as SetEntry).setEntry.setNumber.toInt() - 1).toString()
            index++
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
                        "%.2f".format(metric.toDoubleOrNull() ?: 0.0).toDouble()

                }

                R.id.second_input_field_text -> {
                    (_exercises.value?.get(position) as? SetEntry)?.setEntry?.set?.secondMetric =
                        metric.filterIntegerInput()
                }
            }
        }
    }

    fun onSetTypeChanged(itemPosition: Int, setType: SetType) {
        val captured = _exercises.value.orEmpty()
        (captured[itemPosition] as? SetEntry)?.setEntry?.apply {
            setIndicator = when (setType) {
                SetType.WARMUP -> R.string.warmup_set_indicator
                SetType.DROP_SET -> R.string.drop_set_indicator
                SetType.DEFAULT -> R.string.default_set_indicator
                SetType.FAILURE -> R.string.failure_set_indicator
            }
            set.type = setType
        }
        _exercises.value = captured
    }

    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.minimizeWorkout()
        }
    }

    fun cancel() {
        viewModelScope.launch {
            _name.postValue("")
            _exercises.postValue(listOf())
            workoutProvider.clearWorkoutState()
            workoutStateManager.cancel()
        }
    }

    fun finishWorkout() {
        if (_exercises.value.isNullOrEmpty()) {
            _restTimerState.value = State.Error("Cannot finish a workout without any exercises!")
            return
        }
        if (_exercises.value.orEmpty().all { entry -> entry is ExerciseEntry }) {
            _restTimerState.value = State.Error("Cannot finish a workout without any sets!")
            return
        }
        if (_exercises.value.orEmpty().filterIsInstance<SetEntry>().all {
                (it.setEntry.set.secondMetric ?: 0) == 0 && (it.setEntry.set.firstMetric
                    ?: 0.0) == 0.0
            }) {
            _restTimerState.value = State.Error("Cannot finish a workout with empty sets!")
            return
        }
        viewModelScope.launch {
            val (exercises, prs, volume) = getExerciseArrayAndPRs(_exercises.value.orEmpty())
            workoutId?.let {
                repo.updateWorkoutDate(it, workoutDate)
            }
            repo.addWorkoutToHistory(
                Workout(
                    id = workoutId ?: hashString("${Random().nextInt(Int.MAX_VALUE)}"),
                    name = "${getTimePeriodAsString()} ${_name.value}",
                    date = workoutDate,
                    exercises = exercises,
                    note = _note.value,
                    duration = _timer.value?.parseTimeStringToLong() ?: 0L,
                    isTemplate = false,
                    personalRecords = prs,
                    volume = volume
                )
            )
            _navigate.postValue(R.id.workout_to_finish_workout)
            addExerciseToWorkoutProvider.resetSelectedExercises()
            workoutStateManager.finishWorkout()
            workoutProvider.clearWorkoutState()
            _name.postValue("")
        }
    }

    private suspend fun getExerciseArrayAndPRs(
        entries: List<WorkoutEntry>,
        removeEmpty: Boolean = true,
    ): Triple<List<Exercise>, Int, Double> {
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
                        val reps = entry.setEntry.set.secondMetric
                        val weight = entry.setEntry.set.firstMetric
                        if (weight != null && weight != 0.0 && reps != null && reps != 0) {
                            sets.add(entry.setEntry.set)

                            volume += weight * reps
                            if (entry.setEntry.set.type == SetType.DEFAULT || entry.setEntry.set.type == SetType.FAILURE) {
                                when (category) {
                                    //reps only, cardio, and timed are soonTM
//                                    Category.RepsOnly -> {
//                                        if ((entry.setEntry.set.secondMetric
//                                                ?: 0) > (bestSet.secondMetric ?: 0)
//                                        ) {
//                                            bestSet = entry.setEntry.set
//                                        }
//                                    }
//
//                                    Category.AssistedWeight -> {
//                                        if ((bestSet.firstMetric
//                                                ?: 0.0) < (entry.setEntry.set.firstMetric ?: 0.0)
//                                        ) {
//                                            bestSet = entry.setEntry.set
//                                        }
//                                    }
                                    else -> if (weight > (bestSet.firstMetric ?: 0.0)) bestSet =
                                        entry.setEntry.set
                                }
                            }
                        }
                    }
                }
            }
        }
        if (removeEmpty) exercises.entries.removeIf { it.value.sets.isEmpty()  || it.value.sets.any { set -> set.firstMetric == null || set.firstMetric == 0.0 || set.secondMetric == null || set.secondMetric == 0 } }

        val temp = exercises.values.map { it.copy() }.toMutableList()
        if (removeEmpty) {
            //no need to update exercises when saving state
            temp.forEach { currExercise ->
                templateExercises.find { it.name == currExercise.name }?.let { template ->
                    when (currExercise.category) {
                        //unfortunately due to time constraints special sets were dropped as an idea
//                    Category.RepsOnly -> {
//                        if ((currExercise.bestSet.secondMetric
//                                ?: 0) <= (template.bestSet.secondMetric ?: 0)
//                        ) {
//                            currExercise.bestSet = template.bestSet
//                        } else {
//                            prs++
//                        }
//                    }
                        else -> {
                            val currentBestSetResult = (currExercise.bestSet.firstMetric
                                ?: 0.0) * (currExercise.bestSet.secondMetric ?: 0)
                            val previousBestSetResult =
                                (template.bestSet.firstMetric
                                    ?: 0.0) * (template.bestSet.secondMetric
                                    ?: 0)

                            if (currentBestSetResult <= previousBestSetResult) {
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

    private fun getTimePeriodAsString() = when (workoutDate.hour) {
        in 6..11 -> "Morning"
        in 12..16 -> "Noon"
        in 17..20 -> "Afternoon"
        else -> "Night"
    }

    fun changeNote(itemPosition: Int, text: String) {
        (_exercises.value?.get(itemPosition) as? ExerciseEntry)?.exerciseEntry?.note = text
    }

    private suspend fun saveWorkoutState() {
        val prefixes = setOf("Morning", "Noon", "Afternoon", "Night")
        val (exercises, prs, volume) = getExerciseArrayAndPRs(_exercises.value.orEmpty(), false)

        val workout = Workout(
            id = workoutId ?: hashString("${Random().nextInt(Int.MAX_VALUE)}"),
            name = if (_name.value.isNullOrEmpty() || !(prefixes.any {
                    (_name.value ?: "").contains(
                        it
                    )
                })) "${getTimePeriodAsString()} ${_name.value}" else _name.value ?: "",
            date = workoutDate,
            exercises = exercises,
            note = _note.value,
            duration = _timer.value?.parseTimeStringToLong() ?: 0L,
            isTemplate = false,
            personalRecords = prs,
            volume = volume
        )
        workoutProvider.addWorkoutState(RealmWorkoutState().apply {
            id = workout.id
            name = workout.name
            this.duration = workout.duration ?: 0L
            this.volume = workout.volume ?: 0.0
            date = workout.date.toRealmString()
            isTemplate = false
            this.exercises = workout.exercises.map { it.toRealmExercise() }.toRealmList()
            note = _note.value
            personalRecords = prs
            restTimerStart =
                if (restTimerProvider.isRestActive()) restTimerProvider.getRestStartDate()
                    .toRealmString() else ""
            restTimerEnd = if (restTimerProvider.isRestActive()) restTimerProvider.getEndOfRest()
                .toRealmString() else ""
            timeOfStop = LocalDateTime.now().toRealmString()
        })
        if (restTimerProvider.isRestActive()) restTimerProvider.stopRest()

    }

    //
    sealed interface State {
        data class Default(val restState: Boolean) : State
        data class Rest(val time: String) : State
        data class Error(val message: String?) : State
    }
}