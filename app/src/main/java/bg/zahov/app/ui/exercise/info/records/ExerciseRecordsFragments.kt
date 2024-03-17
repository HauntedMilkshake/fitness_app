package bg.zahov.app.ui.exercise.info.records

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.fitness.app.databinding.FragmentExerciseRecordsBinding

class ExerciseRecordsFragments: Fragment() {
    private var _binding: FragmentExerciseRecordsBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExerciseRecordsBinding.inflate(inflater, container, false)
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