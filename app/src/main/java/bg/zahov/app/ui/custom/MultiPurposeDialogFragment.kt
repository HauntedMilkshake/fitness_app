package bg.zahov.app.ui.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.DialogFragmentRadioButtonBinding

class MultiPurposeDialogFragment: DialogFragment() {
    private var _binding: DialogFragmentRadioButtonBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val titleRes by lazy {
        arguments?.getInt("TITLE") ?: R.string.dialog_fragment_placeholder_title
    }
    private val options by lazy {
        arguments?.getStringArray("OPTIONS")?.toList() ?: emptyList<String>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFragmentRadioButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateRadioGroup()
        binding.apply {
            title.setText(titleRes)
            options.setOnCheckedChangeListener { radioGroup, i ->
                //TODO(Propagate to vm to replace the custom views)
            }
        }
    }

    private fun populateRadioGroup() {
        options.forEachIndexed { index, value ->
            binding.options.addView(RadioButton(context).apply {
                text = value
                id = index
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MultiPurposeDialog"
    }
}