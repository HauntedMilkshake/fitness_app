package bg.zahov.app.ui.exercise.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.AddExerciseUiMapper
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.ui.custom.ExerciseView
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentNewExerciseBinding

class AddExerciseFragment : Fragment() {
    private var _binding: FragmentNewExerciseBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val addExerciseViewModel: AddExerciseViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        binding.apply {
            addExerciseViewModel.state.map { AddExerciseUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    it.notify?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }

                    if (it.isAdded) findNavController().navigate(R.id.add_exercise_to_exercises)
                }

            confirm.setOnClickListener {
                it.applyScaleAnimation()
                addExerciseViewModel.addExercise(exerciseFieldText.text.toString())
            }

            back.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.add_exercise_to_exercises)
            }
        }
    }

    private fun initViews() {
        binding.apply {
            addExerciseViewModel.category.observe(viewLifecycleOwner) {
                Log.d("LIVEDATA", "INVOKE")
                category.initViewInformation(
                    "Body part",
                    listOf(
                        Category.Barbell.name,
                        Category.Dumbbell.name,
                        Category.Machine.name,
                        Category.AdditionalWeight.name,
                        Category.AssistedWeight.name,
                        Category.RepsOnly.name,
                        Category.Cardio.name,
                        Category.Timed.name
                    ),
                    currCategory = it,
                    currBodyPart = null
                )
                category.exerciseChangeListener =
                    object : ExerciseView.ExerciseChangeListener<String> {
                        override fun onOptionClicked(item: String) {
                            addExerciseViewModel.setCategory(item)
                        }
                    }
            }
            addExerciseViewModel.bodyPart.observe(viewLifecycleOwner) {
                Log.d("LIVEDATA", "INVOKE")
                bodyPart.initViewInformation(
                    "Category",
                    listOf(
                        BodyPart.Arms.name,
                        BodyPart.Other.name,
                        BodyPart.Back.name,
                        BodyPart.Chest.name,
                        BodyPart.Core.name,
                        BodyPart.Legs.name,
                        BodyPart.Olympic.name,
                        BodyPart.Shoulders.name
                    ),
                    currBodyPart = it,
                    currCategory = null
                )
                bodyPart.exerciseChangeListener =
                    object : ExerciseView.ExerciseChangeListener<String> {
                        override fun onOptionClicked(item: String) {
                            addExerciseViewModel.setBodyPart(item)
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}