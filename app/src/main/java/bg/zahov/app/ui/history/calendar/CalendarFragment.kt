package bg.zahov.app.ui.history.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentCalendarBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding
        get() = requireNotNull(_binding!!)

    private val calendarViewModel: CalendarViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        requireActivity().hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_calendar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }

        })
        binding.apply {
            calendar.apply {
                monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderViewContainer> {
                    override fun bind(container: MonthHeaderViewContainer, data: CalendarMonth) {
                        container.monthTitle.text = data.yearMonth.month.name
                        container.titlesContainer.apply {
                            children.map { it as? TextView }.forEachIndexed { index, textView ->
                                textView?.text =
                                    daysOfWeek()[index].getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.getDefault()
                                    )
                            }
                        }
                    }

                    override fun create(view: View) = MonthHeaderViewContainer(view)
                }
                monthFooterBinder = object : MonthHeaderFooterBinder<MonthFooterViewContainer> {
                    override fun bind(
                        container: MonthFooterViewContainer,
                        data: CalendarMonth,
                    ) {
                        container.apply {
                            year.text = data.yearMonth.year.toString()
                            calendarViewModel.numberOfWorkouts.observe(viewLifecycleOwner) { map ->
                                map[data.yearMonth.month]?.let {
                                    footerText.text = it.toString()
                                }
                            }
                        }
                    }

                    override fun create(view: View) = MonthFooterViewContainer(view)
                }

                dayBinder = object : MonthDayBinder<DayViewContainer> {
                    override fun bind(container: DayViewContainer, data: CalendarDay) {
                        container.apply {
                            textView.text = data.date.dayOfMonth.toString()
                            calendarViewModel.workoutsPerMonthCheck.observe(viewLifecycleOwner) {
                                it[WorkoutDate(
                                    month = data.date.month,
                                    day = data.date.dayOfMonth
                                )]?.let { visibility ->
                                    checkImage.visibility = visibility
                                }
                            }

                            if (data.position == DayPosition.MonthDate) {
                                textView.setTextColor(Color.WHITE)
                            } else {
                                textView.setTextColor(Color.GRAY)
                            }

                        }
                    }

                    override fun create(view: View) = DayViewContainer(view)
                }

                setup(
                    YearMonth.now().minusMonths(3),
                    YearMonth.now(),
                    daysOfWeek().first()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().hideBottomNav()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}