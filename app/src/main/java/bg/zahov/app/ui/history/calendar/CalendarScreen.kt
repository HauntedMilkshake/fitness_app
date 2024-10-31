package bg.zahov.app.ui.history.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import bg.zahov.fitness.app.R

@Composable
fun CalendarScreen(calendarViewModel: CalendarViewModel = viewModel()) {
    val uiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    CalendarContent(uiState)
}

@Composable
fun CalendarContent(uiState: CalendarUiState) {

    val state = rememberCalendarState(
        startMonth = uiState.startMonth,
        endMonth = uiState.endMonth,
        firstVisibleMonth = uiState.endMonth,
        firstDayOfWeek = uiState.firstDayOfWeek
    )

    HorizontalCalendar(
        state = state,
        dayContent = { Day(it) },
        monthHeader = {
            MonthText(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = "", // determine the current month
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        },
        monthFooter = {
            MonthText(
                modifier = Modifier.fillMaxWidth(),
                text = "", // determine the current month the calendar is on
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
                if (hasUserWorkedOut) colorResource(R.color.blue_text) else colorResource(R.color.background),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString(), color = Color.White)
    }
}

@Composable
fun MonthText(
    modifier: Modifier,
    text: String,
    style: TextStyle,
    fontWeight: FontWeight? = null,
    color: Color
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        fontWeight = fontWeight,
        color = color
    )
}

