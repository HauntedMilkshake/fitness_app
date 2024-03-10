package bg.zahov.app.ui.workout.start

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.StartWorkoutUiMapper
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentStartWorkoutBinding

class StartWorkoutFragment : Fragment() {
    private var _binding: FragmentStartWorkoutBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val startWorkoutViewModel: StartWorkoutViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStartWorkoutBinding.inflate(inflater, container, false)
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
            val workoutAdapter = TemplateWorkoutAdapter().apply {
                itemClickListener = object : TemplateWorkoutAdapter.ItemClickListener<Workout> {
                    override fun onWorkoutClicked(item: Workout, clickedView: View) {
                    }

                    override fun onWorkoutStart(position: Int) {
                        startWorkoutViewModel.startWorkoutFromTemplate(position)
//                        findNavController().navigate(R.id.to_workout_fragment)
                    }

                    override fun onWorkoutDelete(position: Int) {
                        startWorkoutViewModel.deleteTemplateWorkout(position)
                    }

                    override fun onWorkoutDuplicate(position: Int) {
                        startWorkoutViewModel.addDuplicateTemplateWorkout(position)
                    }

                    override fun onWorkoutEdit(item: Workout) {
                        findNavController().navigate(
                            R.id.workout_to_create_workout_template,
                            bundleOf("EDIT" to true, "WORKOUT_ID" to item.id)
                        )
                    }
                }
            }

            templatesRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = workoutAdapter
            }

            startWorkoutViewModel.templates.observe(viewLifecycleOwner) {
                workoutAdapter.updateItems(it)
            }

            startWorkoutViewModel.state.map { StartWorkoutUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    showToast(it.errorMessage)

//                    if (it.shutdown) TODO()
                }

            startEmptyWorkout.setOnClickListener {
                startWorkoutViewModel.startEmptyWorkout()
            }
            addTemplate.setOnClickListener {
                findNavController().navigate(R.id.workout_to_create_workout_template)
            }
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}