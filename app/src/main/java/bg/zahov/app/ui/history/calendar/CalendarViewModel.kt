package bg.zahov.app.ui.history.calendar

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch
import java.time.Month
import java.time.YearMonth

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }

    private val _workoutsPerMonthCheck = MutableLiveData<Map<WorkoutDate, Int>>()
    val workoutsPerMonthCheck: LiveData<Map<WorkoutDate, Int>>
        get() = _workoutsPerMonthCheck


    private val _numberOfWorkouts = MutableLiveData<Map<Month, Int>>()
    val numberOfWorkouts: LiveData<Map<Month, Int>>
        get() = _numberOfWorkouts

    init {
        viewModelScope.launch {
            try {
                workoutProvider.getPastWorkouts().collect { pastWorkouts ->
                    val workoutsPerMonthCheck = mutableMapOf<WorkoutDate, Int>()
                    val workoutsPerMonth = mutableMapOf<Month, Int>()

                    for (i in 0 until 4) {
                        val month = YearMonth.now().minusMonths(i.toLong())
                        val workoutsForMonth =
                            pastWorkouts.filter { workout -> workout.date.month == month.month }
                        val workoutCount = workoutsForMonth.size

                        workoutsPerMonth[month.month] = workoutCount

                        val daysInMonth = month.lengthOfMonth()
                        for (dayOfMonth in 1..daysInMonth) {
                            val workoutDate = WorkoutDate(month.month, dayOfMonth)
                            val dayVisibility = if (workoutsForMonth.any { workout ->
                                    workout.date.dayOfMonth == dayOfMonth
                                }) View.VISIBLE else View.GONE
                            workoutsPerMonthCheck[workoutDate] = dayVisibility
                        }
                    }

                    _workoutsPerMonthCheck.value = workoutsPerMonthCheck.toMap()
                    _numberOfWorkouts.value = workoutsPerMonth.toMap()
                }
            } catch (e: Exception) {
                serviceError.stopApplication()
            }
        }
    }

}

data class WorkoutDate(val month: Month, val day: Int)


