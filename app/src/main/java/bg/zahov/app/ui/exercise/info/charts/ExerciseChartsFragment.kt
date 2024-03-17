package bg.zahov.app.ui.exercise.info.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.databinding.FragmentExerciseChartsBinding

class ExerciseChartsFragment: Fragment() {
    private var _binding: FragmentExerciseChartsBinding? = null
    private val binding
        get() = requireNotNull(_binding)

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

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}