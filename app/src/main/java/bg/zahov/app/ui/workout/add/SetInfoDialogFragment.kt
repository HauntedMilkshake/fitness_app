package bg.zahov.app.ui.workout.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import bg.zahov.app.data.model.SetType
import bg.zahov.fitness.app.databinding.DialogFragmentSetInfoBinding

class SetInfoDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentSetInfoBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val setInfoViewModel: SetInfoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFragmentSetInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            setInfoViewModel.infoType.observe(viewLifecycleOwner) {
                when (it) {
                    SetType.WARMUP -> {

                    }

                    SetType.DROP_SET -> {

                    }

                    SetType.DEFAULT -> {

                    }

                    SetType.FAILURE -> {


                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "SetInfoDialogFragment"
    }
}