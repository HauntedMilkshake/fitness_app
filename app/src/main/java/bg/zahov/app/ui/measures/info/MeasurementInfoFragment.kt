package bg.zahov.app.ui.measures.info

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import bg.zahov.app.data.model.state.MeasurementInformationUiMapper
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showTopBar
import bg.zahov.app.ui.measures.MeasuresFragment.Companion.MEASUREMENT_ARGS
import bg.zahov.app.ui.measures.info.input.MeasurementInputFragment
import bg.zahov.app.util.MonthValueFormatter
import bg.zahov.app.util.RightAxisValueFormatter
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentMeasurementInformationBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate

class MeasurementInfoFragment : Fragment() {
    private var _binding: FragmentMeasurementInformationBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val measurementInfoViewModel: MeasurementInfoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeasurementInformationBinding.inflate(inflater, container, false)
        requireActivity().showTopBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        requireActivity().setToolBarTitle(arguments?.getString(MEASUREMENT_ARGS) ?: "Measurement")
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_measures, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        })
        binding.apply {
            measurementInfoViewModel.state.map { MeasurementInformationUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    circularProgressIndicator.visibility = it.loadingVisibility
                    chart.apply {
                        visibility = it.chartVisibility
                        val dataSet = LineDataSet(it.chartData, "results").apply {
                            valueTextColor = Color.WHITE
                            valueTextSize = 13f
                        }

                        data = LineData(dataSet)
                        notifyDataSetChanged()
                        invalidate()

                        axisRight.apply {
                            axisMaximum = it.max
                            axisMinimum = it.min
                        }
                    }
                    setupChart(it.suffix)

                }
            addEntry.setOnClickListener {
                val measurementInputFragment = MeasurementInputFragment.newInstance(
                    arguments?.getString(MEASUREMENT_ARGS) ?: "Measurement"
                )
                measurementInputFragment.show(childFragmentManager, measurementInputFragment.tag)
            }
        }
    }

    private fun setupChart(suffix: String) {
        binding.chart.apply {
            extraRightOffset = 34f
            setPinchZoom(false)
            setDrawGridBackground(false)
            isDoubleTapToZoomEnabled = false
            legend.isEnabled = false
            axisLeft.isEnabled = false

            description.apply {
                textColor = Color.WHITE
                text = "Measurements"
            }

            xAxis.apply {
                axisMinimum = 1f
                axisMaximum = LocalDate.now().lengthOfMonth().toFloat()
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                valueFormatter = MonthValueFormatter()
            }

            axisRight.apply {
                textColor = Color.WHITE
                granularity = 1f
                valueFormatter = RightAxisValueFormatter(suffix)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}