package bg.zahov.app.ui.exercise.info.charts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.databinding.FragmentExerciseChartsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate

class ExerciseChartsFragment : Fragment() {
    private var _binding: FragmentExerciseChartsBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val exerciseChartViewModel: ExerciseChartViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExerciseChartsBinding.inflate(inflater, container, false)
        exerciseChartViewModel.initChartData()
        requireActivity().hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            exerciseChartViewModel.oneRepMax.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it.third, "results")

                bestOneRepMaxSet.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }

                bestOneRepMaxSet.axisRight.axisMinimum = it.second
                bestOneRepMaxSet.axisRight.axisMaximum = it.first
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 13f
            }

            exerciseChartViewModel.totalVolume.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it.third, "results")
                totalVolume.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }
                totalVolume.axisRight.axisMinimum = it.second
                totalVolume.axisRight.axisMaximum = it.first
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 13f

            }
            exerciseChartViewModel.maxReps.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it.third, "results")
                bestSetReps.axisRight.axisMinimum = it.second
                bestSetReps.axisRight.axisMaximum = it.first
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 13f
                bestSetReps.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }
            }
            setupChart(totalVolume, "Max volume", "kg")
            setupChart(bestOneRepMaxSet, "One rep max estimates", "kg")
            setupChart(bestSetReps, "Max reps", "reps")
        }
    }

    private fun setupChart(chart: LineChart, chartDescription: String, suffix: String) {
        chart.apply {
            setPinchZoom(false)
            setDrawGridBackground(false)
            isDoubleTapToZoomEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled = false

            description.apply {
                textColor = Color.WHITE
                text = chartDescription
            }
            xAxis.apply {
                axisMinimum = 1f
                axisMaximum = LocalDate.now().lengthOfMonth().toFloat()
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
            }
            axisRight.apply {
                textSize = 14f
                textColor = Color.WHITE
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()} $suffix"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}