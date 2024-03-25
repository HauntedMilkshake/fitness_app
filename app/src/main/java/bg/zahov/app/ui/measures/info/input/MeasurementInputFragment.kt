package bg.zahov.app.ui.measures.info.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.state.MeasurementInputUiMapper
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.DialogFragmentMeasurementInputBinding

class MeasurementInputFragment : DialogFragment() {
    companion object {
        private const val ARGUMENT_KEY = "measurement_type"

        fun newInstance(title: String): MeasurementInputFragment {
            val fragment = MeasurementInputFragment()
            val args = Bundle().apply {
                putString(ARGUMENT_KEY, title)
            }
            fragment.arguments = args
            return fragment
        }
    }
    private var _binding: DialogFragmentMeasurementInputBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val measurementInputViewModel: MeasurementInputViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFragmentMeasurementInputBinding.inflate(inflater, container, false)
        measurementInputViewModel.type = arguments?.getString(ARGUMENT_KEY) ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            title.text = arguments?.getString(ARGUMENT_KEY)
            measurementInputViewModel.state.map { MeasurementInputUiMapper.map(it) }.observe(viewLifecycleOwner) {
                if(it.action) dismiss()
                date.text = it.date
            }
            cancel.setOnClickListener {
                dismiss()
            }
            save.setOnClickListener {
                measurementInputViewModel.saveInput(inputFieldText.text.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}