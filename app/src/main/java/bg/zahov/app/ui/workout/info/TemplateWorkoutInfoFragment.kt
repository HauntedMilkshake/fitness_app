package bg.zahov.app.ui.workout.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.state.TemplateWorkoutUiMapper
import bg.zahov.app.ui.exercise.ExerciseAdapter
import bg.zahov.fitness.app.databinding.FragmentTemplateWorkoutInfoBinding

class TemplateWorkoutInfoFragment : Fragment() {
    private var _binding: FragmentTemplateWorkoutInfoBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val workoutName by lazy {
        arguments?.getString("WORKOUT_NAME")
    }

    private val templateWorkoutInfoViewModel: TemplateWorkoutInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTemplateWorkoutInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        templateWorkoutInfoViewModel.workoutName = workoutName
        templateWorkoutInfoViewModel.fetchWorkout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val exerciseAdapter = ExerciseAdapter()

            templateWorkoutInfoViewModel.state.map { TemplateWorkoutUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    lastPerformed.apply {
                        visibility = it.lastPerformedVisibility
                        text = it.lastPerformedText
                    }
                    exercisesRecyclerView.apply {
                        visibility = it.exercisesVisibility
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = exerciseAdapter
                    }
                    circularProgressIndicator.visibility = it.loadingIndicatorVisibility
                    if (it.shutdown) findNavController().navigateUp()
                    exerciseAdapter.updateItems(it.exercises)
                    showToast(it.message)
                }

            startWorkout.setOnClickListener {
                templateWorkoutInfoViewModel.startWorkout()
            }
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}