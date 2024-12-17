package bg.zahov.app.ui.workout.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.hideBottomNav
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.ui.exercise.ExercisesFragment.Companion.ADD_EXERCISE_ARG
import bg.zahov.app.ui.exercise.ExercisesFragment.Companion.REPLACE_EXERCISE_ARG
import bg.zahov.app.ui.exercise.ExercisesFragment.Companion.STATE_ARG
import bg.zahov.app.ui.workout.AddTemplateWorkoutScreen
import bg.zahov.app.ui.workout.start.StartWorkoutFragment.Companion.EDIT_FLAG_ARG_KEY
import bg.zahov.app.ui.workout.start.StartWorkoutFragment.Companion.WORKOUT_ID_ARG_KEY
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.R.menu.menu_toolbar_add_workout
import kotlinx.coroutines.launch

class AddTemplateWorkoutFragment : Fragment() {

    private val addWorkoutViewModel: AddTemplateWorkoutViewModel by viewModels()
    private val toastManager: ToastManager = ToastManager

    private val edit by lazy {
        arguments?.getBoolean(EDIT_FLAG_ARG_KEY) == true
    }

    private val id by lazy {
        arguments?.getString(WORKOUT_ID_ARG_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setupTopBar()
        addWorkoutViewModel.initEditWorkoutId(
            edit,
            id ?: ""
        )
        activity?.hideBottomNav()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AddTemplateWorkoutScreen(
                    addTemplateViewModel = addWorkoutViewModel,
                    onAddExercise = {
                        findNavController().navigate(
                            R.id.create_workout_template_to_add_exercise,
                            bundleOf(STATE_ARG to ADD_EXERCISE_ARG)
                        )
                    },
                    onReplaceExercise = {
                        findNavController().navigate(
                            R.id.create_workout_template_to_add_exercise,
                            bundleOf(STATE_ARG to REPLACE_EXERCISE_ARG)
                        )
                    },
                    onBackPressed = { findNavController().navigateUp() },
                    onCancel = { findNavController().navigateUp() },
                )
            }
        }
    }

    private fun setupTopBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        activity?.setToolBarTitle(if (edit) R.string.edit_workout_template else R.string.new_workout_template)
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(menu_toolbar_add_workout, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.create_workout_template_to_workout)
                    true
                }

                R.id.save -> {
                    addWorkoutViewModel.addTemplateWorkout()
                    true
                }

                else -> false
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                launch {
                    toastManager.messages.collect {
                        it?.let { message ->
                            Toast.makeText(
                                context,
                                context?.getString(message.messageResId),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        addWorkoutViewModel.clearToast()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.hideBottomNav()
    }
}