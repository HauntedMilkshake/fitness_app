package bg.zahov.app.ui.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.local.Workout
import bg.zahov.app.utils.applyScaleAnimation
import bg.zahov.app.ui.adapter.TemplateWorkoutAdapter
import bg.zahov.app.ui.viewmodel.WorkoutViewModel
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentWorkoutBinding

//FIXME clear binding
class WorkoutFragment : Fragment() {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private val workoutViewModel: WorkoutViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
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
            addTemplate.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.workout_to_create_workout_template)
            }

            val workoutAdapter = TemplateWorkoutAdapter().apply {
                object : TemplateWorkoutAdapter.ItemClickListener<Workout> {
                    override fun onSettingsClicked(item: Workout, clickedView: View) {
                        //
                    }

                    override fun onWorkoutClicked(item: Workout, clickedView: View) {

                    }
                }
            }

            templatesRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = workoutAdapter
            }

            workoutViewModel.templates.observe(viewLifecycleOwner) {
                workoutAdapter.updateItems(it)
            }

        }
    }
}