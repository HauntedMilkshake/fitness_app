package bg.zahov.app.ui.history.calendar

import android.view.View
import android.widget.TextView
import bg.zahov.fitness.app.R
import com.kizitonwose.calendar.view.ViewContainer

class MonthFooterViewContainer(view: View) : ViewContainer(view) {
    val footerText: TextView = view.findViewById(R.id.footer)
    val year: TextView = view.findViewById(R.id.year)
}