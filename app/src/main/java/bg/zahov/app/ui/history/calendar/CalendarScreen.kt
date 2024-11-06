package bg.zahov.app.ui.history.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import bg.zahov.fitness.app.R
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

@Composable
fun CalendarScreen(calendarViewModel: CalendarViewModel = viewModel()) {
    val uiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val calendarState = rememberCalendarState(
        startMonth = uiState.startMonth,
        endMonth = uiState.endMonth,
        firstVisibleMonth = uiState.endMonth,
        firstDayOfWeek = uiState.firstDayOfWeek
    )
    CalendarContent(
        calendarState = calendarState,
        dayToHasUserWorkedOut = uiState.dayToHasUserWorkedOut,
        daysOfWeek = uiState.daysOfWeek,
        numberOfWorkoutsPerMonth = uiState.numberOfWorkoutsPerMonth
    )
}

@Composable
fun CalendarContent(
    calendarState: CalendarState,
    daysOfWeek: List<DayOfWeek>,
    dayToHasUserWorkedOut: Map<LocalDate, Boolean>,
    numberOfWorkoutsPerMonth: Map<Month, String>
) {
    HorizontalCalendar(
        state = calendarState,
        dayContent = { Day(it, dayToHasUserWorkedOut[it.date] == true) },
        monthHeader = {
            MonthHeader(
                text = it.yearMonth.month.name,
                daysOfWeek = daysOfWeek
            )
        },
        monthFooter = {
            MonthFooter(
                workoutCount = stringResource(
                    R.string.workouts_for_month,
                    numberOfWorkoutsPerMonth[it.yearMonth.month] ?: 0
                ),
                year = it.yearMonth.year.toString(),
            )
        }
    )
}

@Composable
fun Day(
    day: CalendarDay, hasUserWorkedOut: Boolean = true,
    modifier: Modifier = Modifier
        .aspectRatio(1f)
        .background(
            color = colorResource(R.color.background)
        )
        .padding(4.dp)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) Color.White else Color.Gray
        )
        if (hasUserWorkedOut) {
            Icon(
                painter = painterResource(R.drawable.ic_check_mark),
                contentDescription = "",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun MonthHeader(
    modifier: Modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
    text: String,
    daysOfWeek: List<DayOfWeek> = listOf()
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items(count = daysOfWeek.size) { pos ->
                MonthHeaderDayText(Modifier.weight(1f), daysOfWeek[pos])
            }
        }
    }
}

@Composable
fun MonthHeaderDayText(modifier: Modifier = Modifier, day: DayOfWeek) {
    Text(
        text = day.name.take(3),
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        textAlign = TextAlign.Center
    )
}

@Composable
fun MonthFooter(
    modifier: Modifier = Modifier.fillMaxWidth(),
    year: String,
    workoutCount: String,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            text = year,
            style = MaterialTheme.typography.bodyLarge,
            color = colorResource(R.color.less_vibrant_text)
        )

        Text(
            modifier = Modifier.padding(end = 12.dp, top = 12.dp),
            text = workoutCount,
            style = MaterialTheme.typography.bodyLarge,
            color = colorResource(R.color.less_vibrant_text)
        )
    }
}