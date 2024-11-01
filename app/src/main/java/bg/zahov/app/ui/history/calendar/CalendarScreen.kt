package bg.zahov.app.ui.history.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import bg.zahov.fitness.app.R
import com.kizitonwose.calendar.compose.CalendarState
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
        numberOfWorkoutsPerMonth = uiState.numberOfWorkoutsPerMonth
    )
}

@Composable
fun CalendarContent(
    calendarState: CalendarState,
    dayToHasUserWorkedOut: Map<LocalDate, Boolean>,
    numberOfWorkoutsPerMonth: Map<Month, String>
) {
    HorizontalCalendar(
        state = calendarState,
        dayContent = { Day(it, dayToHasUserWorkedOut[it.date] == true) },
        monthHeader = {
            MonthText(
                boxModifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
                boxAlignment = Alignment.Center,
                textModifier = Modifier,
                text = it.yearMonth.month.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        monthFooter = {
            MonthText(
                boxModifier = Modifier.fillMaxWidth(),
                boxAlignment = Alignment.CenterStart,
                textModifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                text = stringResource(
                    R.string.workouts_for_month,
                    numberOfWorkoutsPerMonth[it.yearMonth.month] ?: "0"
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    )
}

@Composable
fun Day(day: CalendarDay, hasUserWorkedOut: Boolean = true) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = colorResource(R.color.background)
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString(), color = Color.White)
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
fun MonthText(
    boxModifier: Modifier = Modifier,
    boxAlignment: Alignment,
    textModifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    fontWeight: FontWeight? = null,
    color: Color
) {
    Box(modifier = boxModifier, contentAlignment = boxAlignment) {
        Text(
            modifier = textModifier,
            text = text,
            style = style,
            fontWeight = fontWeight,
            color = color
        )
    }
}

