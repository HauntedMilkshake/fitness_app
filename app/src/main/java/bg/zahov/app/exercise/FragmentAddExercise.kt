package bg.zahov.app.exercise

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bg.zahov.app.data.BodyPart
import bg.zahov.app.data.Category
import bg.zahov.fitness.app.databinding.FragmentNewExerciseBinding

class FragmentAddExercise: Fragment() {
    private var _binding: FragmentNewExerciseBinding? = null
    private val binding get() = _binding!!
    private val addExerciseViewModel: AddExerciseViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        binding.apply {
            confirm.setOnClickListener {
                Log.d("TEXT", "TEXT: ${exerciseNameField.editText!!.text!!}")
                addExerciseViewModel.addExercise(exerciseNameField.editText!!.text!!.toString())

            }
        }
    }
    private fun initViews(){
        binding.apply {
            category.initViewInformation("Body part",listOf(BodyPart.Arms.name, BodyPart.Other.name, BodyPart.Back.name, BodyPart.Chest.name, BodyPart.Core.name, BodyPart.Legs.name, BodyPart.Olympic.name, BodyPart.Shoulders.name), addExerciseViewModel)
            bodyPart.initViewInformation("Category",listOf(Category.Barbell.name, Category.Dumbbell.name, Category.Machine.name, Category.AdditionalWeight.name, Category.AssistedWeight.name,Category.RepsOnly.name, Category.Cardio.name, Category.Timed.name), addExerciseViewModel)

        }
    }
}