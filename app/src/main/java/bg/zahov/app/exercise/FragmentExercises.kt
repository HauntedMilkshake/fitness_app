package bg.zahov.app.exercise

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.Filter
import bg.zahov.app.data.SelectableExercise
import bg.zahov.app.utils.applyScaleAnimation
import bg.zahov.app.utils.toSelectableList
import bg.zahov.app.workout.addWorkout.AddWorkoutViewModel
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentExercisesBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.textview.MaterialTextView

class FragmentExercises : Fragment() {
    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!
    private val exerciseViewModel: ExerciseViewModel by viewModels({ requireActivity() })
    private val addWorkoutViewModel: AddWorkoutViewModel by viewModels({ requireActivity() })
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
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
            exerciseText.apply {
                text = if (arguments?.getBoolean("SELECTABLE") == true) "Add exercises" else "Exercises"
            }

            val filterAdapter = FilterAdapter(true).apply {
                itemClickListener = object : FilterAdapter.ItemClickListener<Filter> {
                    override fun onItemClicked(item: Filter, clickedView: View) {
                        exerciseViewModel.removeFilter(item)
                    }
                }
            }
            filterItemsRecyclerView.apply {
                layoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }
                adapter = filterAdapter
            }
            val exerciseAdapter =
                ExerciseAdapter(arguments?.getBoolean("SELECTABLE") ?: false).apply {
                    itemClickListener =
                        object : ExerciseAdapter.ItemClickListener<SelectableExercise> {
                            override fun onItemClicked(
                                item: SelectableExercise,
                                itemPosition: Int,
                                clickedView: View,
                            ) {
                            }
                        }
                }

            exercisesRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = exerciseAdapter
            }

            searchIcon.setOnClickListener {
                it.applyScaleAnimation()
                exerciseText.visibility = View.GONE
                searchIcon.visibility = View.GONE
                settingsDots.visibility = View.GONE
                removeSearchBar.visibility = View.VISIBLE
                searchBar.visibility = View.VISIBLE
                searchBar.onActionViewExpanded()

            }

            settingsFilters.setOnClickListener {
                it.applyScaleAnimation()
                FilterDialog().show(childFragmentManager, FilterDialog.TAG)
            }

            exerciseViewModel.searchFilters.observe(viewLifecycleOwner) { filters ->
                filterAdapter.updateItems(filters)

                searchBar.let {
                    it.setOnQueryTextListener(object :
                        androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            query?.let { name ->
                                exerciseViewModel.searchExercises(name, listOf())
                            }
                            return true
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            query?.let { name ->
                                handler.postDelayed({
                                    exerciseViewModel.searchExercises(name, listOf())
                                }, 1500)
                            }
                            return true
                        }
                    })
                }

            }

            removeSearchBar.setOnClickListener {
                it.applyScaleAnimation()
                exerciseText.visibility = View.VISIBLE
                searchIcon.visibility = View.VISIBLE
                settingsDots.visibility = View.VISIBLE
                searchBar.onActionViewCollapsed()
                searchBar.visibility = View.GONE
                removeSearchBar.visibility = View.GONE
            }

            settingsDots.setOnClickListener {
                it.applyScaleAnimation()
                showCustomLayout()
            }

            exerciseViewModel.userExercises.observe(viewLifecycleOwner) {
                exerciseAdapter.updateItems(it.toSelectableList())
                noResultsLabel.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }

            close.apply {
                visibility =
                    if (arguments?.getBoolean("SELECTABLE") == true) View.VISIBLE else View.GONE
                setOnClickListener {
                    it.applyScaleAnimation()
                    findNavController().navigate(R.id.exercises_to_create_workout_template)
                }
            }

            confirm.apply {
                visibility =
                    if (arguments?.getBoolean("SELECTABLE") == true) View.VISIBLE else View.GONE
                setOnClickListener {
                    it.applyScaleAnimation()
                    Log.d("EXERCISES", exerciseAdapter.getSelected().toString())
                    addWorkoutViewModel.addSelectedExercises(exerciseAdapter.getSelected())
                    findNavController().navigate(R.id.exercises_to_create_workout_template)
                }
            }
        }
    }

    private fun showCustomLayout() {
        val inflater = LayoutInflater.from(requireContext())
        val customView = inflater.inflate(R.layout.simple_popup, null)
        val textView = customView.findViewById<MaterialTextView>(R.id.create_exercise_view)

        val fadeIn = ObjectAnimator.ofFloat(customView, "alpha", 0f, 1f)
        fadeIn.duration = 300

        fadeIn.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                customView.visibility = View.VISIBLE
            }
        })

        fadeIn.start()

        val popupWindow = PopupWindow(
            customView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        textView.setOnClickListener {
            findNavController().navigate(R.id.exercise_to_create_exercise)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(binding.settingsDots, 80, 70)
    }

}