package bg.zahov.app.ui.exercise.info.records

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bg.zahov.fitness.app.databinding.FragmentExerciseRecordsBinding

class ExerciseRecordsFragments : Fragment() {
    private var _binding: FragmentExerciseRecordsBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val exerciseRecordsViewModel: ExerciseRecordsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExerciseRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            exerciseRecordsViewModel.state.observe(viewLifecycleOwner) {
                oneRepMax.text = it.oneRepMax
                maxVolume.text = it.maxVolume
                maxWeight.text = it.maxWeight
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}