package bg.zahov.app.ui.workout.add

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.AddTemplateWorkoutUiMapper
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.ExerciseWithNoteVisibility
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentAddWorkoutTemplateBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class AddTemplateWorkoutFragment : Fragment() {

    private var _binding: FragmentAddWorkoutTemplateBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val addWorkoutViewModel: AddTemplateWorkoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddWorkoutTemplateBinding.inflate(inflater, container, false)
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
                    override fun onSetCheckClicked(item: ClickableSet, clickedView: View) {
                        //NOOP
                    }

                    override fun onAddSet(item: ExerciseWithNoteVisibility, set: ClickableSet) {
                        addWorkoutViewModel.addSet(item, set.set)
                    }

                    override fun onNoteToggle(item: ExerciseWithNoteVisibility) {
                        addWorkoutViewModel.toggleExerciseNoteField(item)
                    }

                    override fun onReplaceExercise(item: ExerciseWithNoteVisibility) {
                        addWorkoutViewModel.setReplaceableExercise(item)
                        findNavController().navigate(
                            R.id.create_workout_template_to_add_exercise,
                            bundleOf("REPLACING" to true)
                        )
                    }

                    override fun onRemoveExercise(item: ExerciseWithNoteVisibility) {
                        addWorkoutViewModel.removeExercise(item)
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
                        addWorkoutViewModel.removeSet(item, set.set)
                    }
                }
                textChangeListener = object : ExerciseSetAdapter.TextActionListener {
                    override fun onInputFieldChanged(
                        exercise: ExerciseWithNoteVisibility,
                        set: ClickableSet,
                        metric: String,
                        id: Int
                    ) {
                        addWorkoutViewModel.onInputFieldTextChanged(exercise, set.set, metric, id)
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

            addWorkoutViewModel.state.map { AddTemplateWorkoutUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    showToast(it.eMessage)
                    showToast(it.nMessage)
                    if (it.success) findNavController().navigate(R.id.create_workout_template_to_workout)


                }

            save.setOnClickListener {
                it.applyScaleAnimation()
                addWorkoutViewModel.workoutName = workoutNameFieldText.text.toString()
                addWorkoutViewModel.workoutNote = workoutNoteFieldText.text.toString()
                addWorkoutViewModel.addWorkout()
            }

            cancel.setOnClickListener {
                it.applyScaleAnimation()
                addWorkoutViewModel.resetSelectedExercises()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.workoutNameFieldText.setText(addWorkoutViewModel.workoutName)
        binding.workoutNoteFieldText.setText(addWorkoutViewModel.workoutNote)
    }

    override fun onPause() {
        super.onPause()
        addWorkoutViewModel.workoutName = binding.workoutNameFieldText.text.toString()
        addWorkoutViewModel.workoutNote = binding.workoutNoteFieldText.text.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}