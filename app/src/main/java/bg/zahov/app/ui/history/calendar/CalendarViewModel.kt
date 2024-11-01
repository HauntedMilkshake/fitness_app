package bg.zahov.app.ui.history.calendar

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
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth


/**
 * Represents the UI state for the calendar, containing information about
 * the selected months, the user's workout data, and other relevant states.
 *
 * @property startMonth The starting month to display on the calendar. Defaults to three months ago.
 * @property endMonth The ending month to display on the calendar. Defaults to the current month.
 * @property firstDayOfWeek The first day of the week based on the user's locale.
 * @property dayToHasUserWorkedOut A map indicating whether the user has worked out on a specific date.
 * @property numberOfWorkoutsPerMonth A map that holds the number of workouts for each month.
 */
data class CalendarUiState(
    val startMonth: YearMonth = YearMonth.now().minusMonths(3),
    val endMonth: YearMonth = YearMonth.now(),
    val firstDayOfWeek: DayOfWeek = firstDayOfWeekFromLocale(),
    val dayToHasUserWorkedOut: Map<LocalDate, Boolean> = mapOf(),
    val numberOfWorkoutsPerMonth: Map<Month, String> = mapOf()
)

/**
 *
 * @property workoutProvider A provider for fetching workout data. Defaults to the injected workout provider.
 * @property serviceError A handler for managing service errors. Defaults to the injected error handler.
 */
class CalendarViewModel(
    private val workoutProvider: WorkoutProvider = Inject.workoutProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState())
    /**
     * The current UI state of the calendar, exposed as a StateFlow for observing changes.
     */
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                workoutProvider.getPastWorkouts().collect { pastWorkouts ->
                    val dayToHasUserWorkedOut = mutableMapOf<LocalDate, Boolean>()
                    val numberOfWorkoutsPerMonth = mutableMapOf<Month, String>()

                    for (i in 0 until 4) {
                        val month = YearMonth.now().minusMonths(i.toLong())
                        val workoutsForMonth =
                            pastWorkouts.filter { workout -> workout.date.month == month.month }
                        val workoutCount = workoutsForMonth.size

                        numberOfWorkoutsPerMonth[month.month] = workoutCount.toString()

                        val daysInMonth = month.lengthOfMonth()
                        for (dayOfMonth in 1..daysInMonth) {
                            val workoutDate =
                                LocalDate.of(Year.now().value, month.month, dayOfMonth)
                            dayToHasUserWorkedOut[workoutDate] =
                                pastWorkouts.any { it.date.toLocalDate() == workoutDate }
                        }
                    }

                    _uiState.update { old ->
                        old.copy(
                            dayToHasUserWorkedOut = dayToHasUserWorkedOut,
                            numberOfWorkoutsPerMonth = numberOfWorkoutsPerMonth
                        )
                    }
                }
            } catch (e: CriticalDataNullException) {
                serviceError.initiateCountdown()
            }
        }
    }
}
