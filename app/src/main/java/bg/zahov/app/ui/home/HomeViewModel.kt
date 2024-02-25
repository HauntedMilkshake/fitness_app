package bg.zahov.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getUserProvider
import bg.zahov.app.getWorkoutProvider
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
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
    val workoutEntries: LiveData<List<BarEntry>>
        get() = _workoutEntries
    init {
        viewModelScope.launch {
            launch {
                userRepo.getUser().collect {
                    _userName.postValue(it.name)
                }
            }
            launch {
                workoutRepo.getPastWorkouts().collect { pastWorkouts ->
                    _numberOfWorkouts.postValue(pastWorkouts.size)
                    val workoutsByWeek = groupWorkoutsByWeek(pastWorkouts)
                    val barEntries = mutableListOf<BarEntry>()

                    workoutsByWeek.forEach { (weekIndex, workoutsInWeek) ->
                        barEntries.add(BarEntry(weekIndex.toFloat(), workoutsInWeek.size.toFloat()))
                    }

                    _workoutEntries.postValue(barEntries)
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
                val workoutDate = LocalDate.parse(workout.date, DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault()))
                workoutDate in startOfWeek..endOfWeek
            }

            workoutsByWeek[weekIndex] = workoutsInWeek.toMutableList()

            weekIndex++
            currentDate = currentDate.plusWeeks(1)
        }

        return workoutsByWeek
    }
}