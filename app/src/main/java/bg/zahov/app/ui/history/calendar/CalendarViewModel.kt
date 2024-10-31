package bg.zahov.app.ui.history.calendar

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth

data class WorkoutDate(val month: Month, val day: Int)

data class CalendarUiState(
    val startMonth: YearMonth = YearMonth.now().minusMonths(3),
    val endMonth: YearMonth = YearMonth.now(),
    val firstDayOfWeek: DayOfWeek = firstDayOfWeekFromLocale(),
    val workoutsPerMonth: Map<WorkoutDate, Int> = mapOf(),
    val numberOfWorkouts: Map<Month, Int> = mapOf()
)

class CalendarViewModel(
    private val workoutProvider: WorkoutProvider = Inject.workoutProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

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

                    _uiState.update { old ->
                        old.copy(
                            workoutsPerMonth = workoutsPerMonthCheck.toMap(),
                            numberOfWorkouts = workoutsPerMonth.toMap()
                        )
                    }
                }
            } catch (e: CriticalDataNullException) {
                serviceError.initiateCountdown()
            }
        }
    }

}