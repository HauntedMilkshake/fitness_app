package bg.zahov.app.ui.history.calendar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import bg.zahov.fitness.app.R
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val checkImage: ImageView = view.findViewById(R.id.check_mark)
    val textView: TextView = view.findViewById(R.id.calendarDayText)
}