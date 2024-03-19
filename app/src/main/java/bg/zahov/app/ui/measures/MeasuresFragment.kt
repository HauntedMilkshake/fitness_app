package bg.zahov.app.ui.measures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentMeasuresBinding

class MeasuresFragment : Fragment() {
    private var _binding: FragmentMeasuresBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val measurementViewModel: MeasuresViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeasuresBinding.inflate(inflater, container, false)
        requireActivity().showTopBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().setToolBarTitle(R.string.measure)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_measures, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        })

        binding.apply {
            measuresRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MeasurementsAdapter().apply {
                    itemClickListener =
                        object : MeasurementsAdapter.ItemClickListener<AdapterMeasurement> {
                            override fun onItemClicked(item: AdapterMeasurement, position: Int) {
                                measurementViewModel.onMeasurementClick(item.title)
                                findNavController().navigate(
                                    R.id.measures_to_measurement_info,
                                    bundleOf(MEASUREMENT_ARGS to item.title)
                                )
                            }
                        }
                    updateItems(enumValues<MeasurementType>().map { enum -> AdapterMeasurement(enum.key) })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MEASUREMENT_ARGS = "measurement name"
    }
}