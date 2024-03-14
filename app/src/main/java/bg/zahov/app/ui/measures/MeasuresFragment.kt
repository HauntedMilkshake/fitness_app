package bg.zahov.app.ui.measures

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bg.zahov.app.setToolBarTitle
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentMeasuresBinding

class MeasuresFragment : Fragment() {
    private var _binding: FragmentMeasuresBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeasuresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setToolBarTitle(R.string.measure)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}