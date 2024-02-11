package bg.zahov.app.ui.workout

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.ui.workout.add.ExerciseSetAdapter
import bg.zahov.app.ui.workout.add.WorkoutEntry
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentWorkoutBinding

class WorkoutFragment : Fragment() {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val onGoingWorkoutViewModel: OnGoingWorkoutViewModel by viewModels()
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            val exerciseSetAdapter = ExerciseSetAdapter().apply {
                itemClickListener = object : ExerciseSetAdapter.ItemClickListener<WorkoutEntry> {
                    override fun onOptionsClicked(item: Exercise, clickedView: View) {
                    }

                    override fun onSetClicked(item: ClickableSet, clickedView: View) {
                    }

                    override fun onSetCheckClicked(item: ClickableSet, clickedView: View) {
                    }

                    override fun onAddSet(item: Exercise, set: ClickableSet) {
//                        addWorkoutViewModel.addSet(item, set.set)
                    }
                }
                swipeActionListener = object : ExerciseSetAdapter.SwipeActionListener {
                    override fun onDeleteSet(item: Exercise, set: ClickableSet) {
                    }
                }

            }

            workoutRecyclerView.apply {
                adapter = exerciseSetAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

//            val swipeGesture = object : SwipeGesture() {
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                    when(direction){
//                        ItemTouchHelper.LEFT -> {
//                            if(viewHolder is  ExerciseSetAdapter.SetViewHolder && viewHolder.itemViewType == R.layout.item_set) {
//                                viewHolder.deleteSet()
//                            }
//                        }
//                    }
//                }
//            }
//            val itemTouchHelper = ItemTouchHelper(swipeGesture)
//            itemTouchHelper.attachToRecyclerView(exercisesRecyclerView)


            addExercise.setOnClickListener {
                findNavController().navigate(
                    R.id.workout_to_add_exercise,
                    bundleOf("SELECTABLE" to true)
                )
            }

            onGoingWorkoutViewModel.workout.observe(viewLifecycleOwner) {
                workoutName.text = it.name
                exerciseSetAdapter.updateItems(it.exercises)
            }

            minimize.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.minimize()
            }

            cancel.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.finishWorkout()
            }
            restTimer.setOnClickListener {
                it.applyScaleAnimation()
            }
            finishText.setOnClickListener {
                it.applyScaleAnimation()
                onGoingWorkoutViewModel.finishWorkout()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}