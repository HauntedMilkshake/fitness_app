package bg.zahov.app.ui.history.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.YearMonth

@Composable
fun CalendarScreen(calendarViewModel: CalendarViewModel = viewModel()) {
    val uiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    CalendarContent()
}

@Composable
fun CalendarContent() {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(3) }
    val endMonth = remember { currentMonth }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    HorizontalCalendar(
        state = state,
        dayContent = { Day(it) }
    )
}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        //we would need to have ana if here for the check mark of whether the user worked out that day or didn't
        Text(text = day.date.dayOfMonth.toString())
    }
}

