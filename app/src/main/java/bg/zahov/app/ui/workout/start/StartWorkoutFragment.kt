package bg.zahov.app.ui.workout.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.StartWorkoutUiMapper
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
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

    override fun onResume() {
        super.onResume()
        requireActivity().showTopBar()
        requireActivity().showBottomNav()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().setToolBarTitle(R.string.workout)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_start_workout, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        })
        binding.apply {
            val workoutAdapter = TemplateWorkoutAdapter().apply {
                itemClickListener = object : TemplateWorkoutAdapter.ItemClickListener<Workout> {
                    override fun onWorkoutClicked(item: Workout, clickedView: View) {
                        findNavController().navigate(R.id.start_workout_to_template_workout_info, bundleOf("WORKOUT_ID" to item.id))
                    }

                    override fun onWorkoutStart(position: Int) {
                        startWorkoutViewModel.startWorkoutFromTemplate(position)
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