package bg.zahov.app.ui.home

import android.app.Application
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
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

                    val workoutsByWeek = groupWorkoutsByWeek(pastWorkouts)
                    val barEntries = mutableListOf<BarEntry>()
                    val xAxisLabels = mutableListOf<String>()

                    // Get the current month and year
                    val currentDate = LocalDate.now()
                    val year = currentDate.year
                    val month = currentDate.month

                    // Iterate over each week of the month
                    val calendar = Calendar.getInstance()
                    calendar.clear()
                    calendar.set(year, month.value - 1, 1) // Set the calendar to the first day of the month
                    val weeksInMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)

                    for (weekIndex in 1..weeksInMonth) {
                        val startDateOfWeek = calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        val endDateOfWeek = startDateOfWeek.plusDays(6)
                        val weekLabel = "${startDateOfWeek.monthValue}/${startDateOfWeek.dayOfMonth}-${endDateOfWeek.monthValue}/${endDateOfWeek.dayOfMonth}"
                        xAxisLabels.add(weekLabel)

                        // Check if there are workouts for this week
                        val workoutsInWeek = workoutsByWeek[weekIndex] ?: emptyList()
                        barEntries.add(BarEntry(weekIndex.toFloat(), workoutsInWeek.size.toFloat()))

                        // Move to the next week
                        calendar.add(Calendar.WEEK_OF_MONTH, 1)
                    }

                    _workoutEntries.postValue(barEntries)
                    _xAxisLabels.postValue(xAxisLabels)
                    _state.postValue(State.Default)
                }

            }
        }
    }

    private fun groupWorkoutsByWeek(workouts: List<Workout>): Map<Int, List<Workout>> {
        val firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())

        val workoutsByWeek = mutableMapOf<Int, MutableList<Workout>>()

        var weekIndex = 1
        var currentDate = firstDayOfMonth

        while (currentDate <= lastDayOfMonth) {
            val startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            val workoutsInWeek = workouts.filter { workout ->
                val workoutDate = LocalDate.parse(
                    workout.date,
                    DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
                )
                workout.date. in startOfWeek..endOfWeek
            }

            workoutsByWeek[weekIndex] = workoutsInWeek.toMutableList()

            weekIndex++
            currentDate = currentDate.plusWeeks(1)
        }

        return workoutsByWeek
    }

    sealed interface State {
        object Default : State
        data class Loading(val isLoading: Boolean) : State
    }
}