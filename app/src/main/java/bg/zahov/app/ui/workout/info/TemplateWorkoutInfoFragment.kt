//package bg.zahov.app.ui.workout.info
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.os.bundleOf
//import androidx.core.view.MenuProvider
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.map
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import bg.zahov.app.data.model.state.TemplateWorkoutUiMapper
//import bg.zahov.app.hideBottomNav
//import bg.zahov.app.setToolBarTitle
//import bg.zahov.app.ui.exercise.ExerciseAdapter
//import bg.zahov.app.ui.workout.start.StartWorkoutFragment.Companion.WORKOUT_ID_ARG_KEY
//import bg.zahov.fitness.app.R
//import bg.zahov.fitness.app.databinding.FragmentTemplateWorkoutInfoBinding
//
//class TemplateWorkoutInfoFragment : Fragment() {
//    private var _binding: FragmentTemplateWorkoutInfoBinding? = null
//    private val binding
//        get() = requireNotNull(_binding)
//
//    private val workoutId by lazy {
//        arguments?.getString(WORKOUT_ID_ARG_KEY) ?: ""
//    }
//
//    private val templateWorkoutInfoViewModel: TemplateWorkoutInfoViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        _binding = FragmentTemplateWorkoutInfoBinding.inflate(inflater, container, false)
//        requireActivity().hideBottomNav()
//        return binding.root
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        templateWorkoutInfoViewModel.workoutId = workoutId
//        templateWorkoutInfoViewModel.fetchWorkout()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        templateWorkoutInfoViewModel.workoutName.observe(viewLifecycleOwner) {
//            requireActivity().setToolBarTitle(it)
//        }
//        requireActivity().addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menu.clear()
//                menuInflater.inflate(R.menu.menu_template_workout_info, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
//                R.id.options -> {
//                    findNavController().navigate(
//                        R.id.template_workout_info_to_edit_template,
//                        bundleOf("EDIT" to true, "WORKOUT_ID" to workoutId)
//                    )
//                    true
//                }
//
//                R.id.duplicate -> {
//                    templateWorkoutInfoViewModel.duplicateWorkout()
//                    true
//                }
//
//                R.id.delete -> {
//                    templateWorkoutInfoViewModel.deleteWorkout()
//                    findNavController().navigateUp()
//                    true
//                }
//
//                else -> false
//            }
//        })
//        binding.apply {
//
//            templateWorkoutInfoViewModel.state.map { TemplateWorkoutUiMapper.map(it) }
//                .observe(viewLifecycleOwner) {
//                    lastPerformed.apply {
//                        visibility = it.lastPerformedVisibility
//                        text = it.lastPerformedText
//                    }
//                    exercisesRecyclerView.apply {
//                        visibility = it.exercisesVisibility
//                        layoutManager = LinearLayoutManager(requireContext())
//                        adapter = ExerciseAdapter().apply {
//                            updateItems(it.exercises)
//                        }
//                    }
//                    circularProgressIndicator.visibility = it.loadingIndicatorVisibility
//                    if (it.deleted) findNavController().navigateUp()
//                    showToast(it.notify)
//                }
//
//            startWorkout.setOnClickListener {
//                templateWorkoutInfoViewModel.startWorkout()
//            }
//        }
//    }
//
//    private fun showToast(message: String? = null) {
//        message?.let {
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        requireActivity().hideBottomNav()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//}