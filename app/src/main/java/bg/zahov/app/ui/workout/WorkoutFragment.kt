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
import bg.zahov.app.data.model.ExerciseWithNoteVisibility
import bg.zahov.app.data.model.OnGoingWorkoutUiMapper
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.hideBottomNav
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
                    override fun onSetCheckClicked(item: ClickableSet, clickedView: View) {
                    }

                    override fun onAddSet(item: ExerciseWithNoteVisibility, set: ClickableSet) {
                        onGoingWorkoutViewModel.addSet(item, set.set)
                    }

                    override fun onNoteToggle(item: ExerciseWithNoteVisibility) {

                    }

                    override fun onReplaceExercise(item: ExerciseWithNoteVisibility) {
                        findNavController().navigate(
                            R.id.workout_to_add_exercise,
                            bundleOf("REPLACEABLE" to true)
                        )
                    }

                    override fun onRemoveExercise(item: ExerciseWithNoteVisibility) {
                        onGoingWorkoutViewModel.removeExercise(item)
                    }

                    override fun onSetTypeChanged(
                        item: ExerciseWithNoteVisibility,
                        set: Sets,
                        setType: SetType
                    ) {
                        TODO("Not yet implemented")
                    }
                }
                swipeActionListener = object : ExerciseSetAdapter.SwipeActionListener {
                    override fun onDeleteSet(item: ExerciseWithNoteVisibility, set: ClickableSet) {
                        onGoingWorkoutViewModel.removeSet(item, set.set)
                    }
                }
                textChangeListener = object : ExerciseSetAdapter.TextActionListener {
                    override fun onInputFieldChanged(
                        exercise: ExerciseWithNoteVisibility,
                        set: ClickableSet,
                        metric: String,
                        id: Int
                    ) {
                        TODO("Not yet implemented")
                    }
                }
            }

            workoutRecyclerView.apply {
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
            val itemTouchHelper = ItemTouchHelper(swipeGesture)
            itemTouchHelper.attachToRecyclerView(workoutRecyclerView)


            addExercise.setOnClickListener {
                findNavController().navigate(
                    R.id.workout_to_add_exercise,
                    bundleOf("ADDABLE" to true)
                )
            }

            onGoingWorkoutViewModel.workout.observe(viewLifecycleOwner) {
                workoutName.text = it.name
                //TODO(CHANGE SIGNATURE OF ONGOINGWORKOUTVIEWMODEL
                exerciseSetAdapter.updateItems(it.exercises.map { exercise ->
                    ExerciseWithNoteVisibility(
                        exercise
                    )
                })
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
                findNavController().navigateUp()
            }

            activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (findNavController().currentDestination?.id == R.id.workout) {
                        onGoingWorkoutViewModel.minimize()
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