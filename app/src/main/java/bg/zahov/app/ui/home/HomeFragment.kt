package bg.zahov.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.clearMenu
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R

class
HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        requireActivity().showBottomNav()
        requireActivity().showTopBar()
        setupTopBar()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
//                homeViewModel, navController = findNavController()
                HomeScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.apply {
//            homeViewModel.state.map { HomeUiMapper.map(it) }.observe(viewLifecycleOwner) {
//                loadingIndicator.visibility = it.loadingVisibility
//                weeklyWorkoutsChart.apply {
//                    visibility = it.chartVisibility
//                    xAxis.apply {
//                        valueFormatter = IndexAxisValueFormatter(it.weekRanges.toTypedArray())
//                        axisMaximum = it.xMax
//                        axisMinimum = it.xMin
//                    }
//                    axisRight.apply {
//                        axisMaximum = it.yMax
//                        axisMinimum = it.yMin
//                    }
//
//                    val dataSet = BarDataSet(it.lineData, "workouts")
//                    dataSet.setDrawValues(false)
//                    val barData = BarData(dataSet)
//                    barData.barWidth = 0.5f
//                    data = barData
//                    weeklyWorkoutsChart.notifyDataSetChanged()
//                    weeklyWorkoutsChart.invalidate()
//                }
//                numberOfWorkouts.apply {
//                    visibility = it.textFieldVisibility
//                    text = getString(R.string.workout_text, it.numberOfWorkouts)
//                }
//            }
//            homeViewModel.userName.observe(viewLifecycleOwner) {
//                profileName.text = it
//            }
//
//            weeklyWorkoutsChart.apply {
//                setFitBars(true)
//                legend.isEnabled = false
//                isDoubleTapToZoomEnabled = false
//                axisLeft.isEnabled = false
//                isDragEnabled = false
//                isHighlightFullBarEnabled = false
//                description.apply {
//                    setPosition(250f, 60f)
//                    text = "Weekly workouts"
//                    textColor = Color.WHITE
//                }
//                xAxis.apply {
//                    position = XAxis.XAxisPosition.BOTTOM
//                    granularity = 1f
//                    axisMinimum = 0f
//                    textColor = Color.WHITE
//                    setCenterAxisLabels(true)
//                    isGranularityEnabled = true
//                }
//                axisRight.apply {
//                    textColor = Color.WHITE
//                    granularity = 1f
//                    setDrawGridLines(false)
//                }
//            }
//        }
    }

    private fun setupTopBar() {

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().setToolBarTitle(R.string.profile)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settings -> {
                        findNavController().navigate(R.id.home_to_settings)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().showBottomNav()
        setupTopBar()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().clearMenu()
    }
}