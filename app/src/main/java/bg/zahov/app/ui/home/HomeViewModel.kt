package bg.zahov.app.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getUserProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.util.toExercise
import bg.zahov.app.util.toLocalDateTimeRlm
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepo by lazy {
        application.getUserProvider()
    }

    private val workoutRepo by lazy {
        application.getWorkoutProvider()
    }
    private val workoutStateManager by lazy {
        application.getWorkoutStateManager()
    }

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _numberOfWorkouts = MutableLiveData<Int>()
    val numberOfWorkouts: LiveData<Int>
        get() = _numberOfWorkouts

    private val _workoutEntries = MutableLiveData<List<BarEntry>>()

    private val _xAxisLabels = MutableLiveData<List<String>>()
    val xAxisLabels: LiveData<List<String>>
        get() = _xAxisLabels

    val workoutEntries: LiveData<List<BarEntry>>
        get() = _workoutEntries

    private val _state = MutableLiveData<State>(State.Default)
    val state: LiveData<State>
        get() = _state


    init {
        checkWorkoutState()
        viewModelScope.launch {
            launch {
                userRepo.getUser().collect {
                    _userName.postValue(it.name)
                }
            }
            launch {
                _state.postValue(State.Loading(true))
                workoutRepo.getPastWorkouts().collect { pastWorkouts ->
                    _numberOfWorkouts.postValue(pastWorkouts.size)
                    _workoutEntries.postValue(getWorkoutsPerWeek(pastWorkouts).map {
                        BarEntry(
                            it.key.toFloat(),
                            it.value.toFloat()
                        )
                    })
                    _state.postValue(State.Default)
                }

            }
        }
    }

    private fun getWorkoutsPerWeek(workouts: List<Workout>): Map<Int, Int> {
        val weekRanges = getWeekRangesForCurrentMonth()
        val workoutsPerWeek = mutableMapOf<Int, Int>().apply {
            for (i in weekRanges.indices) {
                put(i, 0)
            }
        }

        workouts.forEach { workout ->
            val weekRangeIndex = weekRanges.indexOfFirst { weekRange ->
                workout.date.dayOfMonth in weekRange.split(" - ")[0].toInt()..weekRange.split(" - ")[1].toInt()
            }
            val currentCount = workoutsPerWeek.getValue(weekRangeIndex)
            workoutsPerWeek[weekRangeIndex] = currentCount + 1
        }


        return workoutsPerWeek
    }


    fun getWeekRangesForCurrentMonth(): List<String> {
        val today = LocalDate.now()
        val firstDayOfMonth = today.withDayOfMonth(1)
        val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

        val weekRanges = mutableListOf<String>()

        var startOfWeek = firstDayOfMonth
        var endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val formatter = DateTimeFormatter.ofPattern("d")

        while (startOfWeek.isBefore(lastDayOfMonth) || startOfWeek.month == lastDayOfMonth.month) {
            val startDay = startOfWeek.format(formatter)
            val endDay = minOf(endOfWeek, lastDayOfMonth).format(formatter)
            weekRanges.add("$startDay - $endDay")
            startOfWeek = endOfWeek.plusDays(1)
            endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        }

        return weekRanges
    }

    private suspend fun checkPreviousState(previousState: RealmWorkoutState) {
        if (previousState.id != "default") {
            Log.d("date when retrieved", previousState.date)
            val lastTime =
                Duration.between(LocalDateTime.now(), previousState.date.toLocalDateTimeRlm())
            Log.d("date when duration", lastTime.seconds.toString())
            workoutStateManager.startWorkout(
                Workout(
                    id = previousState.id,
                    name = previousState.name,
                    duration = previousState.duration,
                    volume = previousState.volume,
                    date = previousState.date.toLocalDateTimeRlm(),
                    isTemplate = false,
                    exercises = previousState.exercises.mapNotNull { it.toExercise() },
                    note = previousState.note,
                    personalRecords = previousState.personalRecords
                ),
                kotlin.math.abs(lastTime.seconds) * 1000
            )
        }
    }

    private fun checkWorkoutState() {
        viewModelScope.launch {
            async { workoutRepo.getPreviousWorkoutState()?.let { checkPreviousState(it) } }.await()
            workoutRepo.clearWorkoutState()
        }
    }

    sealed interface State {
        object Default : State
        data class Loading(val isLoading: Boolean) : State
    }
}