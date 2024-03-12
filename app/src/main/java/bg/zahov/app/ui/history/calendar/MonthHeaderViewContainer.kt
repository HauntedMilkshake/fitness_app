package bg.zahov.app.ui.history.calendar

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import bg.zahov.fitness.app.R
import com.kizitonwose.calendar.view.ViewContainer

class MonthHeaderViewContainer(view: View) : ViewContainer(view) {
    val monthTitle: TextView = view.findViewById(R.id.monthTitle)
    val titlesContainer: LinearLayout = view.findViewById(R.id.day_titles_container)
}