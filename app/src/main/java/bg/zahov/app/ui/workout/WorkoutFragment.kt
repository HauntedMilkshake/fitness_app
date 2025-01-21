package bg.zahov.app.ui.workout

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.RestState
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.app.ui.exercise.ExercisesFragment.Companion.ADD_EXERCISE_ARG
import bg.zahov.app.ui.exercise.ExercisesFragment.Companion.REPLACE_EXERCISE_ARG
import bg.zahov.app.ui.exercise.ExercisesFragment.Companion.STATE_ARG
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentWorkoutBinding
import kotlinx.coroutines.launch

class WorkoutFragment : Fragment() {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val onGoingWorkoutViewModel: WorkoutViewModel by activityViewModels()
    private var onBackPressedCallback: OnBackPressedCallback? = null
    private val toastManager: ToastManager = ToastManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        activity?.hideTopBar()

        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)

        binding.workoutComposable.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding.workoutComposable.setContent {
            WorkoutScreen(
                workoutViewModel = onGoingWorkoutViewModel,
                onAddExercise = {
                    findNavController().navigate(
                        R.id.workout_to_add_exercise,
                        bundleOf(STATE_ARG to ADD_EXERCISE_ARG)
                    )
                },
                onReplaceExercise = {
                    findNavController().navigate(
                        R.id.workout_to_add_exercise,
                        bundleOf(STATE_ARG to REPLACE_EXERCISE_ARG)
                    )
                },
                onBackPressed = {
                    if (findNavController().currentDestination?.id == R.id.workout) {
                        onGoingWorkoutViewModel.minimize()
                        activity?.showBottomNav()
                    }
                    findNavController().navigateUp()
                },
                onCancel = {
                    findNavController().navigateUp()
                }
            )
        }

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
        exitTransition = inflater.inflateTransition(R.transition.fade_out)
        activity?.hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.hideTopBar()
        binding.apply {

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
                            onGoingWorkoutViewModel.clearToast()
                        }
                    }

                    launch {
                        onGoingWorkoutViewModel.uiState.collect {
                            timer.text = it.timer
                            restTimerIndicator.visibility =
                                if (it.restState == RestState.Active) View.VISIBLE else View.GONE
                            restTimerCounter.visibility =
                                if (it.restState == RestState.Active) View.VISIBLE else View.GONE
                            restTimerCounter.text = it.restTimer

                            if (it.isFinished) findNavController().navigate(R.id.workout_to_finish_workout)
                        }
                    }
                }
            }
            minimize.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.minimize()
                activity?.showBottomNav()
                findNavController().navigateUp()
            }
            restTimer.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.workout_to_rest_timer)
            }
            finishText.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.finishWorkout()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.hideTopBar()
        activity?.hideBottomNav()
    }

    override fun onStop() {
        super.onStop()
        activity?.showTopBar()
        activity?.showBottomNav()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null

    }
}