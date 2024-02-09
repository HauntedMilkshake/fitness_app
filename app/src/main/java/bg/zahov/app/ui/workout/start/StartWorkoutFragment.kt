package bg.zahov.app.ui.workout.start

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.StartWorkoutUiMapper
import bg.zahov.app.ui.workout.TemplateWorkoutAdapter
import bg.zahov.app.util.applyScaleAnimation
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
                object : TemplateWorkoutAdapter.ItemClickListener<Workout> {
                    override fun onSettingsClicked(item: Workout, clickedView: View) {
                        //TODO(Open popup with options to start workout
                    }

                    override fun onWorkoutClicked(item: Workout, clickedView: View) {
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

            startWorkoutViewModel.state.map { StartWorkoutUiMapper.map(it) }.observe(viewLifecycleOwner) {
                it.errorMessage?.let {message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }

                it.message?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }

                if (it.shutdown) {
                    //TODO()
                }

                addTemplate.setOnClickListener {view ->
                    view.applyScaleAnimation()
                    if(!it.isWorkoutActive){
                        findNavController().navigate(R.id.workout_to_create_workout_template)
                    }
                }
            }

            startEmptyWorkout.setOnClickListener {
                startWorkoutViewModel.startEmptyWorkout()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}