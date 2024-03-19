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
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

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
        requireActivity().hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            exerciseChartViewModel.oneRepMax.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it, "results")
                bestOneRepMaxSet.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }
            }

            exerciseChartViewModel.totalVolume.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it, "results")
                totalVolume.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }
            }
            exerciseChartViewModel.bestSet.observe(viewLifecycleOwner) {
                val dataSet = LineDataSet(it, "results")
                totalVolume.apply {
                    data = LineData(dataSet)
                    notifyDataSetChanged()
                    invalidate()
                }
            }
            bestOneRepMaxSet.apply {
                setPinchZoom(false)
                description.textColor = Color.WHITE
                legend.isEnabled = false
                xAxis.apply {
                    textColor = Color.WHITE
                }
                axisLeft.isEnabled = false
                axisRight.apply {
                    textColor = Color.WHITE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}