package bg.zahov.app.ui.workout.add

import android.os.Bundle
import android.transition.TransitionInflater
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
import bg.zahov.app.data.model.state.AddTemplateWorkoutUiMapper
import bg.zahov.app.data.model.SetType
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.util.SetSwipeGesture
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentAddWorkoutTemplateBinding

class AddTemplateWorkoutFragment : Fragment() {

    private var _binding: FragmentAddWorkoutTemplateBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val addWorkoutViewModel: AddTemplateWorkoutViewModel by viewModels()

    private val edit by lazy {
        arguments?.getBoolean("EDIT") ?: false
    }

    private val id by lazy {
        arguments?.getString("WORKOUT_ID")
    }

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
        addWorkoutViewModel.initEditWorkoutId(
            edit,
            id ?: ""
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            newWorkoutText.setText(if (edit) R.string.edit_workout_template else R.string.new_workout_template)

            stopCreatingWorkout.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }

            val exerciseSetAdapter = ExerciseSetAdapter().apply {
                itemClickListener = object : ExerciseSetAdapter.ItemClickListener<WorkoutEntry> {
                    override fun onSetCheckClicked(itemPosition: Int) {
                        addWorkoutViewModel.onSetCheckClicked(itemPosition)
                    }

                    override fun onAddSet(itemPosition: Int) {
                        addWorkoutViewModel.addSet(itemPosition)
                    }

                    override fun onNoteToggle(itemPosition: Int) {
                        addWorkoutViewModel.toggleExerciseNoteField(itemPosition)
                    }

                    override fun onReplaceExercise(itemPosition: Int) {
                        addWorkoutViewModel.setReplaceableExercise(itemPosition)
                        findNavController().navigate(
                            R.id.create_workout_template_to_add_exercise,
                            bundleOf("REPLACING" to true)
                        )
                    }

                    override fun onRemoveExercise(itemPosition: Int) {
                        addWorkoutViewModel.removeExercise(itemPosition)
                    }

                    override fun onSetTypeChanged(itemPosition: Int, setType: SetType) {
                        addWorkoutViewModel.onSetTypeChanged(itemPosition, setType)
                    }
                }
                swipeActionListener = object : ExerciseSetAdapter.SwipeActionListener {
                    override fun onDeleteSet(itemPosition: Int) {
                        addWorkoutViewModel.removeSet(itemPosition)
                    }
                }
                textChangeListener = object : ExerciseSetAdapter.TextActionListener {
                    override fun onInputFieldChanged(itemPosition: Int, metric: String, id: Int) {
                        addWorkoutViewModel.onInputFieldChanged(itemPosition, metric, id)
                    }

                    override fun onNoteChanged(itemPosition: Int, text: String) {
                        addWorkoutViewModel.changeNote(itemPosition, text)
                    }
                }
            }

            exercisesRecyclerView.apply {
                adapter = exerciseSetAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            ItemTouchHelper(SetSwipeGesture()).attachToRecyclerView(exercisesRecyclerView)

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
                addWorkoutViewModel.saveTemplateWorkout()
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