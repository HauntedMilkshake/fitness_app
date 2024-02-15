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
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.AddTemplateWorkoutUiMapper
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Sets
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentNewWorkoutTemplateBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class NewWorkoutTemplateFragment : Fragment() {

    private var _binding: FragmentNewWorkoutTemplateBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val addWorkoutViewModel: AddWorkoutViewModel by viewModels()

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
                        showExerciseMenu(item, clickedView)
                    }

                    override fun onSetClicked(item: ClickableSet, clickedView: View) {
                        showSetMenu(item.set, clickedView)
                    }

                    override fun onSetCheckClicked(item: ClickableSet, clickedView: View) {
                        //NOOP
                    }

                    override fun onAddSet(item: Exercise, set: ClickableSet) {
                        addWorkoutViewModel.addSet(item, set.set)
                    }
                }
                swipeActionListener = object : ExerciseSetAdapter.SwipeActionListener {
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

            addWorkoutViewModel.state.map { AddTemplateWorkoutUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    showToast(it.eMessage)
                    showToast(it.nMessage)

                }

            save.setOnClickListener {
                it.applyScaleAnimation()
                addWorkoutViewModel.setWorkoutName(workoutNameFieldText.text.toString())
                addWorkoutViewModel.addWorkout()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }

            cancel.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.create_workout_template_to_workout)
            }
        }
    }

    private fun showExerciseMenu(exercise: Exercise, view: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.MyPopUp), view)
        popupMenu.menuInflater.inflate(R.menu.popup_exercise_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_note -> {
                    binding
                }

                R.id.action_replace -> {
                    addWorkoutViewModel.setReplaceableExercise(exercise)
                    findNavController().navigate(R.id.create_workout_template_to_add_exercise, bundleOf("SELECTABLE" to true, "REPLACING" to true))
                    //TODO(add exercises except they replace)
                }

                R.id.action_remove -> {
                    addWorkoutViewModel.removeExercise(exercise)
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun showSetMenu(set: Sets, view: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.MyPopUp), view)
        popupMenu.menuInflater.inflate(R.menu.popup_set_menu, popupMenu.menu)

        popupMenu.menu.forEach {
            it.setActionView(R.layout.item_menu_popup_set)

            it.actionView?.findViewById<MaterialTextView>(R.id.action)?.setText(
                when (it.itemId) {
                    R.id.action_drop_set -> R.string.drop_set
                    R.id.action_failure_set -> R.string.failure_set
                    R.id.action_warmup_set -> R.string.warmup_set
                    else -> {
                        R.string.drop_set
                    }
                }
            )

            it.actionView?.findViewById<ConstraintLayout>(R.id.item)?.setOnClickListener {
                //TODO(Propagate to adapter / update live data)
            }

            it.actionView?.findViewById<ShapeableImageView>(R.id.openInfo)?.setOnClickListener {
                SetInfoDialogFragment().show(childFragmentManager, SetInfoDialogFragment.TAG)
            }
        }

        popupMenu.show()
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        addWorkoutViewModel.workoutName.observe(viewLifecycleOwner) {
            binding.workoutNameFieldText.setText(it)
        }
    }

    override fun onPause() {
        super.onPause()
        addWorkoutViewModel.setWorkoutName(binding.workoutNameFieldText.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}