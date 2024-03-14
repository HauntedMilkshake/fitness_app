package bg.zahov.app.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.state.HomeUiMapper
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        requireActivity().showBottomNav()
        requireActivity().showTopBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setToolBarTitle(R.string.profile)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settings -> {
                        findNavController().navigate(R.id.settings)
                        true
                    }

                    else -> false
                }
            }

        })
        binding.apply {
//            requireActivity().setToolbarVisibility(View.VISIBLE)
//            requireActivity().setToolbarTitle(R.string.top_bar_text)
//            settings.setOnClickListener {
//            }

            homeViewModel.state.map { HomeUiMapper.map(it) }.observe(viewLifecycleOwner) {
                loadingIndicator.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                weeklyWorkoutsChart.visibility = if (it.isLoading) View.GONE else View.VISIBLE
            }
            homeViewModel.userName.observe(viewLifecycleOwner) {
                profileName.text = it
            }

            homeViewModel.numberOfWorkouts.observe(viewLifecycleOwner) {
                numberOfWorkouts.text = it.toString()
            }

            homeViewModel.workoutEntries.observe(viewLifecycleOwner) {
                val dataSet = BarDataSet(it, "workouts")
                dataSet.setDrawValues(false)
                val data = BarData(dataSet)
                weeklyWorkoutsChart.data = data
                weeklyWorkoutsChart.notifyDataSetChanged()
                weeklyWorkoutsChart.invalidate()
            }

            weeklyWorkoutsChart.apply {
                setPinchZoom(false)
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(false)
                isDragEnabled = false
                isHighlightFullBarEnabled = false

                description.setPosition(250f, 60f)
                description.text = "Weekly workouts"
                description.textColor = Color.WHITE

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(
                        homeViewModel.getWeekRangesForCurrentMonth().toTypedArray()
                    )
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    axisMinimum = 0f
                    textColor = Color.WHITE
                    axisMaximum =
                        (homeViewModel.getWeekRangesForCurrentMonth().size).toFloat()
                    setCenterAxisLabels(true)
                    isGranularityEnabled = true
                }

                axisRight.apply {
                    textColor = Color.WHITE
                    granularity = 1f
                    axisMinimum = 0f
                }

                axisLeft.isEnabled = false
                isDoubleTapToZoomEnabled = false
                setFitBars(true)
                legend.isEnabled = false
            }

        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}