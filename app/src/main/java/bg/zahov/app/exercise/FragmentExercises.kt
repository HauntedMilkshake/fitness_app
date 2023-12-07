package bg.zahov.app.exercise

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.realm_db.Exercise
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentExercisesBinding
import com.google.android.material.textview.MaterialTextView

class FragmentExercises : Fragment() {
    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!
    private val exerciseViewModel: ExerciseViewModel by viewModels()
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

            val exerciseAdapter = ExerciseAdapter().apply {
                itemClickListener = object : ExerciseAdapter.ItemClickListener<Exercise> {
                    override fun onItemClicked(
                        item: Exercise,
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
            exerciseViewModel.userExercises.observe(viewLifecycleOwner) {
                exerciseAdapter.updateItems(it)
            }
            searchIcon.setOnClickListener {
                exerciseText.visibility = View.GONE
                searchIcon.visibility = View.GONE
                settingsDots.visibility = View.GONE
                removeSearchBar.visibility = View.VISIBLE
                searchBar.visibility = View.VISIBLE
            }
            removeSearchBar.setOnClickListener {
                if (searchBar.visibility == View.VISIBLE) {
                    exerciseText.visibility = View.VISIBLE
                    searchIcon.visibility = View.VISIBLE
                    settingsDots.visibility = View.VISIBLE
                    searchBar.visibility = View.INVISIBLE
                    removeSearchBar.visibility = View.GONE

                    searchBar.setQuery("search exercises", true)
                }
            }
            settingsDots.setOnClickListener {
                val scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(
                    it,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f)
                )
                scaleAnimation.duration = 200
                scaleAnimation.repeatCount = 1
                scaleAnimation.repeatMode = ObjectAnimator.REVERSE

                scaleAnimation.start()

                showCustomLayout()
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