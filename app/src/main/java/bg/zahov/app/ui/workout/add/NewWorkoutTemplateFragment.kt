package bg.zahov.app.ui.workout.add

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.AddTemplateWorkoutUiMapper
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.SwipeGesture
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentNewWorkoutTemplateBinding

class NewWorkoutTemplateFragment : Fragment() {
    private var _binding: FragmentNewWorkoutTemplateBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val addWorkoutViewModel: AddWorkoutViewModel by viewModels({ requireActivity() })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewWorkoutTemplateBinding.inflate(inflater, container, false)
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
            stopCreatingWorkout.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }


            val exerciseSetAdapter = ExerciseSetAdapter().apply {
                itemClickListener = object : ExerciseSetAdapter.ItemClickListener<WorkoutEntry> {
                    override fun onOptionsClicked(item: Exercise, clickedView: View) {
                        TODO("Not yet implemented")
                    }

                    override fun onSetClicked(item: ClickableSet, clickedView: View) {
                        TODO("Not yet implemented")
                    }

                    override fun onSetCheckClicked(item: ClickableSet, clickedView: View) {
                        TODO("Not yet implemented")
                    }

                    override fun onAddSet(item: Exercise, set: ClickableSet) {
                        Log.d("ONADDSET", "FRAG,EMT")
                        addWorkoutViewModel.addSet(item, set.set)
                    }
                }
                swipeActionListener = object: ExerciseSetAdapter.SwipeActionListener {
                    override fun onDeleteSet(item: Exercise, set: ClickableSet) {
                        addWorkoutViewModel.removeSet(item, set.set)
                    }
                }

            }

            exercisesRecyclerView.apply {
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
                    R.id.create_workout_template_to_add_exercise,
                    bundleOf("SELECTABLE" to true)
                )
            }

            addWorkoutViewModel.currExercises.observe(viewLifecycleOwner) {
                exerciseSetAdapter.updateItems(it)
            }

            addWorkoutViewModel.state.map { AddTemplateWorkoutUiMapper.map(it) }.observe(viewLifecycleOwner) {
                showToast(it.eMessage)
                if(showToast(it.nMessage)) {
                    findNavController().navigate(R.id.create_workout_template_to_workout)
                }
            }

            save.setOnClickListener {
                it.applyScaleAnimation()
                addWorkoutViewModel.addWorkout(workoutNameFieldText.text.toString(), )
            }

            cancel.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }
        }
    }

    fun showToast(message: String?): Boolean {
        return message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            true
        } ?: false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}