package bg.zahov.app.ui.measures.info.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.DialogFragmentMeasurementInputBinding

class MeasurementInputFragment : DialogFragment() {
    private var _binding: DialogFragmentMeasurementInputBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFragmentMeasurementInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cancel.setOnClickListener {
                findNavController().navigate(R.id.measurement_input_to_measurement_info)
            }
            save.setOnClickListener {
                findNavController().navigate(R.id.measurement_input_to_measurement_info)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}