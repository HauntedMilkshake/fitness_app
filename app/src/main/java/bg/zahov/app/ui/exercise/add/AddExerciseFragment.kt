package bg.zahov.app.ui.exercise.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentNewExerciseBinding

//FIXME binding should be set to null in onDestroyView
class AddExerciseFragment : Fragment() {
    private var _binding: FragmentNewExerciseBinding? = null
    private val binding get() = _binding!!
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
            confirm.setOnClickListener {
                it.applyScaleAnimation()
                addExerciseViewModel.addExercise(exerciseNameField.editText!!.text!!.toString()) { success, message ->
                    if (success) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.add_exercise_to_exercises)
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            back.setOnClickListener {
                it.applyScaleAnimation()
                findNavController().navigate(R.id.add_exercise_to_exercises)
            }
        }
    }

    //FIXME use string resources for labels, not hardcoded strings
    // See related comments in ExerciseView
    private fun initViews() {
        binding.apply {
            category.initViewInformation(
                "Body part",
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
                addExerciseViewModel
            )
            bodyPart.initViewInformation(
                "Category",
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
                addExerciseViewModel
            )

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}