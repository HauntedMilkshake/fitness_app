package bg.zahov.app.ui.workout.add

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.state.AddTemplateWorkoutUiMapper
import bg.zahov.app.data.model.SetType
import bg.zahov.app.hideBottomNav
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.ui.exercise.ExercisesFragment.Companion.REPLACE_EXERCISE_ARG
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
        Log.d("getting edit", arguments?.getString("WORKOUT_ID").toString())
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
        Log.d("on create", "on create")
        addWorkoutViewModel.initEditWorkoutId(
            edit,
            id ?: ""
        )

        requireActivity().hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        requireActivity().setToolBarTitle(if (edit) R.string.edit_workout_template else R.string.new_workout_template)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_add_workout, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.create_workout_template_to_workout)
                    true
                }

                R.id.save -> {
                    addWorkoutViewModel.saveTemplateWorkout()
                    true
                }

                else -> false
            }
        })
        binding.apply {

            workoutNameFieldText.apply {
                setText(addWorkoutViewModel.workoutName)
                addTextChangedListener {
                    addWorkoutViewModel.onWorkoutNameChange(it.toString())
                }
            }
            workoutNoteFieldText.apply {
                setText(addWorkoutViewModel.workoutNote)
                addTextChangedListener {
                    addWorkoutViewModel.onWorkoutNoteChange(it.toString())
                }
            }
            addWorkoutViewModel.workoutName = binding.workoutNameFieldText.text.toString()
            addWorkoutViewModel.workoutNote = binding.workoutNoteFieldText.text.toString()
            val exerciseSetAdapter = ExerciseSetAdapter().apply {
                itemClickListener = object : ExerciseSetAdapter.ItemClickListener<WorkoutEntry> {
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
                            bundleOf(REPLACE_EXERCISE_ARG to true)
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
                    bundleOf("ADDABLE" to true)
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
        requireActivity().hideBottomNav()
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