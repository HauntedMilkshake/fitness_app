package bg.zahov.app.ui.home

import android.app.Application
import android.view.View
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.transition.Visibility
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getRestTimerProvider
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getUserProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.util.toExercise
import bg.zahov.app.util.toLocalDateTimeRlm
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val workoutRestManager by lazy {
        application.getRestTimerProvider()
    }
    private val serviceErrorHandler by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state = _state.asStateFlow()

    init {
        checkWorkoutState()
        viewModelScope.launch {
            launch {
                try {
                    userRepo.getUser().collect {
                        _state.value = HomeState.Default(username = it.name)
                    }
                } catch (e: CriticalDataNullException) {
                }
            }
            launch {
                try {
                    workoutRepo.getPastWorkouts().collect { pastWorkouts ->
                        val workoutPerWeekMap = getWorkoutsPerWeek(pastWorkouts)
                        val barData = workoutPerWeekMap.map {
                            BarEntry(
                                it.key.toFloat(),
                                it.value.toFloat()
                            )
                        }
                        _state.postValue(
                            State.BarData(
                                chartData = barData,
                                numberOfWorkouts = pastWorkouts.size,
                                xMin = workoutPerWeekMap.keys.min().toFloat(),
                                xMax = workoutPerWeekMap.keys.max().toFloat(),
                                yMin = workoutPerWeekMap.values.min().toFloat(),
                                yMax = workoutPerWeekMap.values.max().toFloat(),
                                getWeekRangesForCurrentMonth()
                            )
                        )
                    }
                } catch (e: CriticalDataNullException) {
                    serviceErrorHandler.initiateCountdown()
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


    private fun getWeekRangesForCurrentMonth(): List<String> {
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
            val lastTime =
                Duration.between(LocalDateTime.now(), previousState.date.toLocalDateTimeRlm())
            if (previousState.restTimerStart.isNotEmpty() && previousState.restTimerEnd.isNotEmpty() && !LocalDateTime.now()
                    .isAfter(previousState.restTimerEnd.toLocalDateTimeRlm())
            ) {
                val restDuration = Duration.between(
                    previousState.restTimerStart.toLocalDateTimeRlm(),
                    previousState.restTimerEnd.toLocalDateTimeRlm()
                ).seconds * 1000
                val elapsedTime = Duration.between(
                    previousState.restTimerStart.toLocalDateTimeRlm(),
                    LocalDateTime.now()
                ).seconds * 1000
                workoutRestManager.startRest(restDuration, elapsedTime)
            }

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
                kotlin.math.abs(lastTime.seconds) * 1000,
                true
            )
        }
    }

    private fun checkWorkoutState() {
        viewModelScope.launch {
            async { workoutRepo.getPreviousWorkoutState()?.let { checkPreviousState(it) } }.await()
            workoutRepo.clearWorkoutState()
        }
    }

    sealed interface HomeState {
        object Loading : HomeState
        data class Default(val username: String, val numberOfWorkouts: String = "", val barData: BarData = BarData())
    }

    data class BarData(
        var chartVisibility: Int = View.GONE,
        var xMin: Float = 0f,
        var xMax: Float = 0f,
        var yMin: Float = 0f,
        var yMax: Float = 0f,
        var chartData: List<BarEntry> = listOf(),
        var weekRanges: List<String> = listOf(),
        var xValueFormatter: ValueFormatter = IndexAxisValueFormatter(weekRanges.toTypedArray())
    )
}