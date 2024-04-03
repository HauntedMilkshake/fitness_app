package bg.zahov.app.ui.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import bg.zahov.app.data.model.state.ShutdownFragmentUiMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.databinding.FragmentShuttingDownBinding

class FragmentShuttingDown : Fragment() {
    private var _binding: FragmentShuttingDownBinding? = null
    private val binding
        get() = requireNotNull(_binding!!)
    private val viewModel: ShuttingDownViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentShuttingDownBinding.inflate(inflater, container, false)
        requireActivity().hideTopBar()
        requireActivity().hideBottomNav()
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.state.map { ShutdownFragmentUiMapper.map(it) }.observe(viewLifecycleOwner) {
                countdownText.text = it.timer
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}