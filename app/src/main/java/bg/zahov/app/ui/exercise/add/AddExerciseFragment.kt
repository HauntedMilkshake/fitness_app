package bg.zahov.app.ui.exercise.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.state.AddExerciseUiMapper
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.hideBottomNav
import bg.zahov.app.setToolBarTitle
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
        requireActivity().hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setToolBarTitle(R.string.add_exercise)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_new_exercise, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }
        })

        initViews()
        binding.apply {
            addExerciseViewModel.state.map { AddExerciseUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    it.notify?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                    it.action?.let { action -> findNavController().navigate(action) }
                }

            confirm.setOnClickListener {
                it.applyScaleAnimation()
                addExerciseViewModel.addExercise(exerciseFieldText.text.toString())
            }
        }
    }

    private fun initViews() {
        binding.apply {
            addExerciseViewModel.category.observe(viewLifecycleOwner) {
                category.initViewInformation(
                    "Category",
                    enumValues<Category>().map { enum -> enum.name },
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
                bodyPart.initViewInformation(
                    "Body part",
                    enumValues<BodyPart>().map { enum -> enum.name },
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

    override fun onResume() {
        super.onResume()
        requireActivity().hideBottomNav()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}