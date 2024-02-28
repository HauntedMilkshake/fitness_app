package bg.zahov.app.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getUserProvider
import bg.zahov.app.getWorkoutProvider
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepo by lazy {
        application.getUserProvider()
    }

    private val workoutRepo by lazy {
        application.getWorkoutProvider()
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

    private fun getWorkoutsPerWeek(workouts: List<Workout>): Map<Int, Int> =
        workouts.groupBy {
            it.date.get(WeekFields.of(Locale.getDefault()).weekOfMonth())
        }.mapValues { (_, workoutsInWeek) -> workoutsInWeek.size }

    fun getWeekRangesForCurrentMonth(): List<String> {
        val today = LocalDate.now()
        val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

        val weekRanges = mutableListOf<ClosedRange<LocalDate>>()
        var startOfWeek = firstDayOfMonth
        var endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        while (startOfWeek.isBefore(lastDayOfMonth)) {
            weekRanges.add(startOfWeek..endOfWeek.minusDays(1))
            startOfWeek = endOfWeek
            endOfWeek = startOfWeek.plusDays(7)
            if (endOfWeek > lastDayOfMonth) {
                endOfWeek = lastDayOfMonth.plusDays(1)
            }
        }

        return weekRanges.map { "${it.start.dayOfMonth} - ${it.endInclusive.dayOfMonth}"}
    }

    sealed interface State {
        object Default : State
        data class Loading(val isLoading: Boolean) : State
    }
}