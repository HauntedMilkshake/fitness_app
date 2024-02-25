package bg.zahov.app.ui.workout

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.InteractableExerciseWrapper
import bg.zahov.app.data.model.state.OnGoingWorkoutUiMapper
import bg.zahov.app.data.model.SetType
import bg.zahov.app.hideBottomNav
import bg.zahov.app.showBottomNav
import bg.zahov.app.ui.workout.add.ExerciseSetAdapter
import bg.zahov.app.ui.workout.add.WorkoutEntry
import bg.zahov.app.util.SwipeGesture
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentWorkoutBinding

class WorkoutFragment : Fragment() {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val onGoingWorkoutViewModel: WorkoutViewModel by viewModels()

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
        requireActivity().hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            onGoingWorkoutViewModel.restTimer.map { OnGoingWorkoutUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    restTimerIndicator.visibility = if (it.isRestActive) View.VISIBLE else View.GONE
                    restTimerCounter.visibility = if (it.isRestActive) View.VISIBLE else View.GONE
                    restTimerCounter.text = it.rest
                }

            val exerciseSetAdapter = ExerciseSetAdapter().apply {
                itemClickListener = object : ExerciseSetAdapter.ItemClickListener<WorkoutEntry> {
                    override fun onSetCheckClicked(
                        exercise: InteractableExerciseWrapper,
                        set: ClickableSet,
                        clickedView: View,
                    ) {
                        onGoingWorkoutViewModel.onSetCheckClicked(exercise, set)
                    }

                    override fun onAddSet(item: InteractableExerciseWrapper, set: ClickableSet) {
                        onGoingWorkoutViewModel.addSet(item, set)
                    }

                    override fun onNoteToggle(itemPosition: Int) {
                        onGoingWorkoutViewModel.onNoteToggle(itemPosition)
                    }

                    override fun onReplaceExercise(item: InteractableExerciseWrapper) {
                        onGoingWorkoutViewModel.onExerciseReplace(item)
                        findNavController().navigate(
                            R.id.workout_to_add_exercise,
                            bundleOf("REPLACEABLE" to true)
                        )
                    }

                    override fun onRemoveExercise(item: InteractableExerciseWrapper) {
                        onGoingWorkoutViewModel.removeExercise(item)
                    }

                    override fun onSetTypeChanged(
                        item: InteractableExerciseWrapper,
                        set: ClickableSet,
                        setType: SetType,
                    ) {
                        onGoingWorkoutViewModel.onSetTypeChanged(item, set, setType)
                    }

                }
                swipeActionListener = object : ExerciseSetAdapter.SwipeActionListener {
                    override fun onDeleteSet(item: InteractableExerciseWrapper, set: ClickableSet) {
                        onGoingWorkoutViewModel.removeSet(item, set)
                    }

                }
                textChangeListener = object : ExerciseSetAdapter.TextActionListener {
                    override fun onInputFieldChanged(
                        exercise: InteractableExerciseWrapper,
                        set: ClickableSet,
                        metric: String,
                        id: Int,
                    ) {
                        onGoingWorkoutViewModel.onInputFieldTextChanged(exercise, set, metric, id)
                    }
                }
            }

            exercisesRecyclerView.apply {
                adapter = exerciseSetAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            val swipeGesture = object : SwipeGesture() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            if (viewHolder is ExerciseSetAdapter.SetViewHolder && viewHolder.itemViewType == R.layout.item_set) {
                                viewHolder.deleteSet()
                            }
                        }
                    }
                }
            }

            onGoingWorkoutViewModel.exercises.observe(viewLifecycleOwner) {
                exerciseSetAdapter.updateItems(it)
            }

            onGoingWorkoutViewModel.name.observe(viewLifecycleOwner) {
                workoutName.text = it
            }

            onGoingWorkoutViewModel.note.observe(viewLifecycleOwner) {
                workoutNoteFieldText.setText(it)
            }

            val itemTouchHelper = ItemTouchHelper(swipeGesture)
            itemTouchHelper.attachToRecyclerView(exercisesRecyclerView)

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
                findNavController().navigateUp()
            }

            cancel.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.cancel()
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
                findNavController().navigateUp()
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
    }
}