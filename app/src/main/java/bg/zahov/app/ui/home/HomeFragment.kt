package bg.zahov.app.ui.home

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.showBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

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
            settings.setOnClickListener {
                findNavController().navigate(R.id.home_to_settings)
            }

            homeViewModel.userName.observe(viewLifecycleOwner) {
                profileName.text = it
            }

            homeViewModel.numberOfWorkouts.observe(viewLifecycleOwner) {
                numberOfWorkouts.text = it.toString()
            }

            homeViewModel.xAxisLabels.observe(viewLifecycleOwner) {
                weeklyWorkoutsChart.apply {
                    xAxis.valueFormatter = IndexAxisValueFormatter(it)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxis.setCenterAxisLabels(true)
                    xAxis.isGranularityEnabled = true
                }
            }

            homeViewModel.workoutEntries.observe(viewLifecycleOwner) {
                val dataSet = BarDataSet(it, "workouts")
                dataSet.setDrawValues(false)
                dataSet.barBorderWidth = 1f
                val data = BarData(dataSet)
                data.barWidth = 0.7F

                weeklyWorkoutsChart.data = data
                weeklyWorkoutsChart.invalidate()
            }

            weeklyWorkoutsChart.apply {
                setPinchZoom(false)
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(false)
                isHighlightFullBarEnabled = false

                description.setPosition(250f, 60f)
                description.text = "Weekly workouts"
                description.textColor = Color.WHITE

                xAxis.apply {
                    isEnabled = false
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.WHITE
                }

                legend.textColor = Color.WHITE
                axisLeft.isEnabled = false
                axisRight.textColor = Color.WHITE
                isDragEnabled = false
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