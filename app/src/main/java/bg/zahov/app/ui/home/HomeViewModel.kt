package bg.zahov.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.RestProvider
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutActions
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.provider.UserProviderImpl
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/**
 * Represents the UI state for the Home screen.
 *
 * @property numberOfWorkouts The count of workouts completed.
 * @property data Data for the bar chart visualization.
 * @property isChartLoading Indicates whether the bar chart data is still loading.
 */
data class HomeUiState(
    val username: String = "",
    val numberOfWorkouts: String = "",
    val data: ChartData = ChartData(),
    val isChartLoading: Boolean = true,
)

/**
 * Represents the data for the bar chart visualization.
 *
 * @property xMin Minimum value on the X-axis(not very useful in our case where we show week ranges but still required).
 * @property xMax Maximum value on the X-axis.
 * @property yMin Minimum value on the Y-axis(lowest number of workouts per week).
 * @property yMax Maximum value on the Y-axis(highest number of workouts per week).
 * @property chartData The list of bar entries for the chart(BarEntry - a double (x,y) where x is where we have to place it on the x-axis and y is the value.
 * @property weekRanges The range of weeks for the X-axis labels(for example for 10/24 they would look like 7-13, 14-20 and etc.)
 */
data class ChartData(
    val xMin: Float = 0f,
    val xMax: Float = 0f,
    val yMin: Float = 0f,
    val yMax: Float = 0f,
    val chartData: List<BarEntry> = listOf(),
    val weekRanges: List<String> = listOf(),
)

/**
 * @param userRepo - access to everything related to the user ( in here we only use the username)
 * @param workoutRepo - access to history of previous workouts
 * @param workoutStateManager - check local db(realm) if a workout is stored in it(whenever the user clears the app and reopens it) and starts it again
 * @param workoutRestManager - makes sure to resume previous rest if the app has been opened and it would still need to be running
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepo: UserProviderImpl,
    private val workoutRepo: WorkoutProvider,
    private val workoutStateManager: WorkoutActions,
    private val workoutRestManager: RestProvider,
    private val serviceErrorHandler: ServiceErrorHandler,
) : ViewModel() {

    /**
     * @property uiState
     * Holds the UI state for the Home screen.
     *
     * @property _uiState
     * This property uses a MutableStateFlow to manage and emit changes
     * to the HomeUiState, which can be observed by UI components.
     */
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    /**
     * fetch data asap
     * convert past workouts to appropriate chart information
     * check if we stopped the app during a workout
     */
    init {
        checkWorkoutState()
        viewModelScope.launch {
            launch {
                try {
                    userRepo.getUser().collect { user ->
                        _uiState.update { old ->
                            old.copy(username = user.name)
                        }
                    }
                } catch (e: CriticalDataNullException) {
                    serviceErrorHandler.initiateCountdown()
                }
            }
            launch {
                try {
                    workoutRepo.getCurrentMonthWorkouts()
                        .collect { pastWorkouts ->
                            val workoutPerWeekMap =
                                getWorkoutsPerWeek(pastWorkouts)
                            val barData = workoutPerWeekMap.map {
                                BarEntry(
                                    it.key.toFloat(),
                                    it.value.toFloat()
                                )
                            }

                            _uiState.update { old ->
                                old.copy(
                                    numberOfWorkouts = pastWorkouts.size.toString(),
                                    data = ChartData(
                                        xMax = workoutPerWeekMap.keys.size.toFloat(),
                                        xMin = workoutPerWeekMap.keys.min().toFloat(),
                                        yMax = workoutPerWeekMap.values.max().toFloat(),
                                        yMin = workoutPerWeekMap.values.min().toFloat(),
                                        chartData = barData,
                                        weekRanges = getWeekRangesForCurrentMonth()
                                    ),
                                    isChartLoading = false
                                )
                            }

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

    /**
     * we only want to show information for the current month, and we also need to provide them for the x axis of the barChart
     */
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


    /**
     * if stored isn't default the means we must resume a workout
     */
    private suspend fun checkPreviousState(previousState: String = "") {
        workoutStateManager.startWorkout(
            Workout()
//                kotlin.math.abs(lastTime.seconds) * 1000,
//                true
        )
    }

    /**
     * making sure we don't need to resume a workout before clearing it
     */
    private fun checkWorkoutState() {
        viewModelScope.launch {
//            async { workoutRepo.getPreviousWorkoutState()?.let { checkPreviousState(it) } }.await()
            workoutRepo.clearWorkoutState()
        }
    }
}
