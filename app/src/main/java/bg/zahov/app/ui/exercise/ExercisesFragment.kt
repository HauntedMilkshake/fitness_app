package bg.zahov.app.ui.exercise

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
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.state.ExerciseUiMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.app.ui.exercise.filter.FilterAdapter
import bg.zahov.app.ui.exercise.filter.FilterDialog
import bg.zahov.app.ui.exercise.filter.FilterWrapper
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentExercisesBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class ExercisesFragment : Fragment() {
    private var _binding: FragmentExercisesBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    private val selectable by lazy {
        arguments?.getBoolean("SELECTABLE") ?: false
    }

    private val replaceable by lazy {
        arguments?.getBoolean("REPLACING") ?: false
    }

    private val addable by lazy {
        arguments?.getBoolean("ADDABLE") ?: false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        exerciseViewModel.replaceable = replaceable
        exerciseViewModel.selectable = selectable && !replaceable
        exerciseViewModel.addable = addable
        requireActivity().showTopBar()
        if (addable || selectable || replaceable) requireActivity().hideBottomNav() else requireActivity().showBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(selectable || replaceable || addable)
            (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.menu_toolbar_exercises, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.home -> {
                            findNavController().navigateUp()
                            true
                        }

                        R.id.search -> {
                            (menuItem.actionView as? androidx.appcompat.widget.SearchView)?.apply {
                                setOnQueryTextListener(object :
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

                            true
                        }

                        R.id.filter -> {
                            FilterDialog().show(
                                requireActivity().supportFragmentManager,
                                FilterDialog.TAG
                            )
                            true
                        }

                        R.id.add -> {
                            findNavController().navigate(R.id.exercise_to_create_exercise)
                            true
                        }

                        else -> false
                    }
                }
            }, viewLifecycleOwner)

            requireActivity().setToolBarTitle(
                when {
                    selectable || addable -> {
                        R.string.add_exercise
                    }

                    replaceable -> {
                        R.string.replace_exercise
                    }

                    else -> {
                        R.string.exercise
                    }
                }
            )

            val filterAdapter = FilterAdapter(View.VISIBLE).apply {
                itemClickListener = object : FilterAdapter.ItemClickListener<FilterWrapper> {
                    override fun onItemClicked(item: FilterWrapper, clickedView: View) {
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
                ExerciseAdapter().apply {
                    itemClickListener =
                        object : ExerciseAdapter.ItemClickListener<ExerciseAdapterWrapper> {
                            override fun onItemClicked(
                                item: ExerciseAdapterWrapper,
                                position: Int,
                            ) {
                                when {
                                    replaceable || selectable || addable -> exerciseViewModel.onExerciseClicked(
                                        position
                                    )

                                    else -> {
                                        exerciseViewModel.setClickedExercise(item.name)
                                        findNavController().navigate(R.id.exercises_to_exercise_info_navigation)
                                    }
                                }
                            }
                        }
                }

            exercisesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = exerciseAdapter
            }

            exerciseViewModel.userExercises.observe(viewLifecycleOwner) {
                exerciseAdapter.updateItems(it)
            }

            exerciseViewModel.state.map { ExerciseUiMapper.map(it) }.observe(viewLifecycleOwner) {
                circularProgressIndicator.visibility = it.loadingVisibility
                noResultsLabel.visibility = it.noResultsVisibility
                it.error?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            confirm.apply {
                visibility = if (selectable || replaceable || addable) View.VISIBLE else View.GONE
                setOnClickListener {
                    it.applyScaleAnimation()
                    exerciseViewModel.confirmSelectedExercises()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {}
    override fun onPause() {
        super.onPause()
        exerciseViewModel.onConfirm()
    }

    override fun onResume() {
        super.onResume()
        if (addable || selectable || replaceable) requireActivity().hideBottomNav() else requireActivity().showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}