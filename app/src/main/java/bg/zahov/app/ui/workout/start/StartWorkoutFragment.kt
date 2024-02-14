package bg.zahov.app.ui.workout.start

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.StartWorkoutUiMapper
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
                    override fun onSettingsClicked(item: Workout, clickedView: View) {
                        showCustomLayout(item, clickedView)
                    }

                    override fun onWorkoutClicked(item: Workout, clickedView: View) {
                        //TODO(CUSTOM WORKOUT FRAGMENT)
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

                    if (it.shutdown) {
                        //TODO()
                    }
                }

            startEmptyWorkout.setOnClickListener {
                startWorkoutViewModel.startEmptyWorkout()
            }
            addTemplate.setOnClickListener {
                findNavController().navigate(R.id.workout_to_create_workout_template)
            }
        }
    }


    private fun showCustomLayout(workout: Workout, view: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.MyPopUp), view)
        popupMenu.menuInflater.inflate(R.menu.popup_workout_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    startWorkoutViewModel.deleteTemplateWorkout(workout)

                }

                R.id.action_duplicate -> {
                    startWorkoutViewModel.addDuplicateTemplateWorkout(workout)
                }

                R.id.action_edit -> {
                    //TODO(Go to fragment workout with edit preference
                }

                R.id.action_start_workout -> {
                    startWorkoutViewModel.startWorkoutFromTemplate(workout)
                }
            }
            true
        }
        popupMenu.show()
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