package bg.zahov.app.ui.workout

import android.media.MediaPlayer
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.state.OnGoingWorkoutUiMapper
import bg.zahov.app.data.model.SetType
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.app.showBottomNav
import bg.zahov.app.ui.workout.add.ExerciseSetAdapter
import bg.zahov.app.ui.workout.add.WorkoutEntry
import bg.zahov.app.util.SetSwipeGesture
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentWorkoutBinding

class WorkoutFragment : Fragment() {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val onGoingWorkoutViewModel: WorkoutViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
        exitTransition = inflater.inflateTransition(R.transition.fade_out)
        mediaPlayer = MediaPlayer.create(context, R.raw.nsfw)
        requireActivity().hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().hideTopBar()

        binding.apply {
            onGoingWorkoutViewModel.restTimer.map { OnGoingWorkoutUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    restTimerIndicator.visibility = it.restTimerVisibility
                    restTimerCounter.visibility = it.restTimerVisibility
                    restTimerCounter.text = it.rest
                    it.message?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }

            val exerciseSetAdapter = ExerciseSetAdapter(mediaPlayer).apply {
                itemClickListener = object : ExerciseSetAdapter.ItemClickListener<WorkoutEntry> {
                    override fun onSetCheckClicked(itemPosition: Int) {
                        onGoingWorkoutViewModel.onSetCheckClicked(itemPosition)
                    }

                    override fun onAddSet(itemPosition: Int) {
                        onGoingWorkoutViewModel.addSet(itemPosition)
                    }

                    override fun onNoteToggle(itemPosition: Int) {
                        onGoingWorkoutViewModel.toggleExerciseNoteField(itemPosition)
                    }

                    override fun onReplaceExercise(itemPosition: Int) {
                        onGoingWorkoutViewModel.onExerciseReplace(itemPosition)
                        findNavController().navigate(
                            R.id.workout_to_add_exercise,
                            bundleOf("REPLACEABLE" to true)
                        )
                    }

                    override fun onRemoveExercise(itemPosition: Int) {
                        onGoingWorkoutViewModel.removeExercise(itemPosition)
                    }

                    override fun onSetTypeChanged(itemPosition: Int, setType: SetType) {
                        onGoingWorkoutViewModel.onSetTypeChanged(itemPosition, setType)
                    }

                }
                swipeActionListener = object : ExerciseSetAdapter.SwipeActionListener {
                    override fun onDeleteSet(itemPosition: Int) {
                        onGoingWorkoutViewModel.removeSet(itemPosition)
                    }

                }
                textChangeListener = object : ExerciseSetAdapter.TextActionListener {
                    override fun onInputFieldChanged(
                        itemPosition: Int,
                        metric: String,
                        id: Int,
                    ) {
                        onGoingWorkoutViewModel.onInputFieldChanged(itemPosition, metric, id)
                    }

                    override fun onNoteChanged(itemPosition: Int, text: String) {
                        onGoingWorkoutViewModel.changeNote(itemPosition, text)
                    }
                }
            }

            exercisesRecyclerView.apply {
                adapter = exerciseSetAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            ItemTouchHelper(SetSwipeGesture()).attachToRecyclerView(exercisesRecyclerView)

            onGoingWorkoutViewModel.exercises.observe(viewLifecycleOwner) {
                exerciseSetAdapter.updateItems(it)
            }

            onGoingWorkoutViewModel.name.observe(viewLifecycleOwner) {
                workoutName.text = it
            }

            onGoingWorkoutViewModel.note.observe(viewLifecycleOwner) {
                workoutNoteFieldText.setText(it)
            }

            addExercise.setOnClickListener {
                findNavController().navigate(
                    R.id.workout_to_add_exercise,
                    bundleOf("ADDABLE" to true)
                )
            }

            onGoingWorkoutViewModel.timer.observe(viewLifecycleOwner) {
                timer.text = it
            }

            minimize.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.minimize()
                requireActivity().showBottomNav()
                findNavController().navigateUp()
            }

            cancel.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.cancel()
                requireActivity().showBottomNav()
                findNavController().navigateUp()

            }

            restTimer.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.workout_to_rest_timer)
            }

            finishText.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.finishWorkout()
                requireActivity().showBottomNav()

            }
            onGoingWorkoutViewModel.navigate.observe(viewLifecycleOwner) {
                it?.let { findNavController().navigate(it) }
            }
            activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (findNavController().currentDestination?.id == R.id.workout) {
                        onGoingWorkoutViewModel.minimize()
                        requireActivity().showBottomNav()
                    }
                    findNavController().navigateUp()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}