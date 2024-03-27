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
                val dataSet = LineDataSet(it.second, "results")

                bestOneRepMaxSet.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }

                bestOneRepMaxSet.axisRight.axisMinimum = 0f
                bestOneRepMaxSet.axisRight.axisMaximum = it.first
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 13f
            }

            exerciseChartViewModel.totalVolume.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it.second, "results")
                totalVolume.axisRight.axisMinimum = 0f
                totalVolume.axisRight.axisMaximum = it.first
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 13f
                totalVolume.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }
            }
            exerciseChartViewModel.maxReps.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it.second, "results")
                bestSetReps.axisRight.axisMinimum = 0f
                bestSetReps.axisRight.axisMaximum = it.first
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 13f
                bestSetReps.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }
            }
            setupChart(bestOneRepMaxSet, "One rep max")
            setupChart(totalVolume, "Max volume")
            setupChart(bestSetReps, "Max reps")
        }
    }

    private fun setupChart(chart: LineChart, chartDescription: String) {
        chart.apply {
            setPinchZoom(false)
            setDrawGridBackground(false)
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
//                valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return if (value.toInt() + 1 in 1..LocalDate.now().lengthOfMonth()) {
//                            (value.toInt() + 1).toString()
//                        } else {
//                            ""
//                        }
//                    }
//                }
            }
            axisRight.apply {
                textSize = 14f
                textColor = Color.WHITE
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()} kg"
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