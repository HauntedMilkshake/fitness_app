package bg.zahov.app.ui.home

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.state.HomeUiMapper
import bg.zahov.app.showBottomNav
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
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().showBottomNav()
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
        exitTransition = inflater.inflateTransition(R.transition.fade_out)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
//            requireActivity().setToolbarVisibility(View.VISIBLE)
//            requireActivity().setToolbarTitle(R.string.top_bar_text)
            settings.setOnClickListener {
                findNavController().navigate(R.id.home_to_settings)
            }

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