package bg.zahov.app.workout.addWorkout

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.backend.Sets
import bg.zahov.app.backend.Workout
import bg.zahov.app.common.BaseAdapter
import bg.zahov.app.data.ClickableSet
import bg.zahov.app.utils.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentNewWorkoutTemplateBinding

class FragmentNewWorkoutTemplate : Fragment() {
    private var _binding: FragmentNewWorkoutTemplateBinding? = null
    private val binding get() = _binding!!
    private val addWorkoutViewModel: AddWorkoutViewModel by viewModels({ requireActivity() })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewWorkoutTemplateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
        exitTransition = inflater.inflateTransition(R.transition.fade_out)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            stopCreatingWorkout.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }

            val exerciseSetAdapter = ExerciseSetAdapter().apply {
                object : ExerciseSetAdapter.ItemClickListener<Workout> {
                    override fun onOptionsClicked(item: Workout, clickedView: View) {
                        //TODO(Open popup)
                    }

                    override fun onSetClicked(item: ClickableSet, clickedView: View) {
                        TODO("Not yet implemented")
                    }

                }
            }

            exercisesRecyclerView.apply {
                adapter = exerciseSetAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            addExercise.setOnClickListener {
                findNavController().navigate(
                    R.id.create_workout_template_to_add_exercise,
                    bundleOf("SELECTABLE" to true)
                )
            }

            addWorkoutViewModel.currExercises.observe(viewLifecycleOwner) {
                exerciseSetAdapter.updateItems(it)
            }

            save.setOnClickListener {
                it.applyScaleAnimation()
//                addWorkoutViewModel.addWorkout(workoutNameFieldText.text.toString(), )
//                findNavController().navigate(R.id.create_workout_template_to_workout)
            }

            cancel.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }
        }
    }
}