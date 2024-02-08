package bg.zahov.app.ui.exercise

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.ExerciseUiMapper
import bg.zahov.app.data.model.SelectableFilter
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.app.util.toSelectableList
import bg.zahov.app.ui.workout.add.AddWorkoutViewModel
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentExercisesBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.textview.MaterialTextView

class ExercisesFragment : Fragment() {
    private var _binding: FragmentExercisesBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val exerciseViewModel: ExerciseViewModel by viewModels({ requireActivity() })
    private val addWorkoutViewModel: AddWorkoutViewModel by viewModels({ requireActivity() })
    private val selectable by lazy {
        arguments?.getBoolean("SELECTABLE") ?: false
    }
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
            exerciseText.text =
                if (selectable) "Add exercises" else "Exercises"

            val filterAdapter = FilterAdapter(true).apply {
                itemClickListener = object : FilterAdapter.ItemClickListener<SelectableFilter> {
                    override fun onItemClicked(item: SelectableFilter, clickedView: View) {
                        exerciseViewModel.removeFilter(item)
                    }
                }
            }

            filterItemsRecyclerView.apply {
                layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }
                adapter = filterAdapter
            }

            exerciseViewModel.searchFilters.observe(viewLifecycleOwner) {
                filterAdapter.updateItems(it)
            }

            val exerciseAdapter =
                ExerciseAdapter(selectable).apply {
                    itemClickListener =
                        object : ExerciseAdapter.ItemClickListener<SelectableExercise> {
                            override fun onItemClicked(
                                item: SelectableExercise,
                                itemPosition: Int,
                                clickedView: View,
                            ) {
                                if(selectable) {
                                    if(item.isSelected) addWorkoutViewModel.addExercise(item.exercise) else addWorkoutViewModel.removeExercise(item.exercise)
                                } else {
                                    //GO TO EXERCISE INFO FRAGMENT
                                }
                            }
                        }
                }

            exercisesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = exerciseAdapter
            }

            exerciseViewModel.userExercises.observe(viewLifecycleOwner) {
                exerciseAdapter.updateItems(it.toSelectableList())
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

            exerciseViewModel.state.map { ExerciseUiMapper.map(it) }.observe(viewLifecycleOwner) {
                circularProgressIndicator.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                noResultsLabel.visibility = if (it.areThereResults) View.GONE else View.VISIBLE

                it.error?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            searchBar.let { search ->
                search.setOnQueryTextListener(object :
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { name ->
                            exerciseViewModel.searchExercises(name)
                        }
                        return true
                    }

                    override fun onQueryTextChange(query: String?): Boolean {
                        query?.let { name ->
                            exerciseViewModel.searchExercises(name)
                        }
                        return true
                    }
                })
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

            close.apply {
                visibility =
                    if (selectable) View.VISIBLE else View.GONE
                setOnClickListener {
                    it.applyScaleAnimation()
                    findNavController().navigate(R.id.exercises_to_create_workout_template)
                }
            }

            confirm.apply {
                visibility =
                    if (selectable) View.VISIBLE else View.GONE
                setOnClickListener {
                    it.applyScaleAnimation()
                    findNavController().navigate(R.id.exercises_to_create_workout_template)
                }
            }

            settingsFilters.setOnClickListener {
                it.applyScaleAnimation()
                FilterDialog().show(childFragmentManager, FilterDialog.TAG)
            }
        }
    }

    private fun showCustomLayout() {
        val inflater = LayoutInflater.from(requireContext())
        val customView = inflater.inflate(R.layout.popup_simple, null)
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

    override fun onResume() {
        super.onResume()
        exerciseViewModel.getExercises()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}