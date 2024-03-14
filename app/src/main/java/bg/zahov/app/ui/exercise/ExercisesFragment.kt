package bg.zahov.app.ui.exercise

import android.app.SearchManager
import android.content.ComponentName
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.state.ExerciseUiMapper
import bg.zahov.app.data.model.SelectableFilter
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showTopBar
import bg.zahov.app.ui.exercise.filter.FilterAdapter
import bg.zahov.app.ui.exercise.filter.FilterDialog
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentExercisesBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.grpc.Context

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
    private var searchView: SearchView? = null

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
                    searchView = (menu.findItem(R.id.search).actionView as? SearchView)
                    menuInflater.inflate(R.menu.menu_toolbar_exercises, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.home -> {
                            findNavController().popBackStack()
                            true
                        }

                        R.id.search -> {
                            true
                        }

                        R.id.filter -> {
                            FilterDialog().show(childFragmentManager, FilterDialog.TAG)
                            true
                        }

                        R.id.add -> {
                            showExerciseMenu(requireView())
                            true
                        }

                        else -> false
                    }
                }

            })

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

            searchView?.apply {
                setOnQueryTextListener(object : OnQueryTextListener {
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
                                    //else -> TODO(Exercise fragment)
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

//            searchIcon.setOnClickListener {
//                it.applyScaleAnimation()
//                exerciseText.visibility = View.GONE
//                searchIcon.visibility = View.GONE
//                settingsDots.visibility = View.GONE
//                removeSearchBar.visibility = View.VISIBLE
//                searchBar.visibility = View.VISIBLE
//                searchBar.onActionViewExpanded()
//
//            }

            exerciseViewModel.state.map { ExerciseUiMapper.map(it) }.observe(viewLifecycleOwner) {
                circularProgressIndicator.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                noResultsLabel.visibility = if (it.areThereResults) View.GONE else View.VISIBLE

                it.error?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

//            removeSearchBar.setOnClickListener {
//                it.applyScaleAnimation()
//                exerciseText.visibility = View.VISIBLE
//                searchIcon.visibility = View.VISIBLE
//                settingsDots.visibility = View.VISIBLE
//                searchBar.onActionViewCollapsed()
//                searchBar.visibility = View.GONE
//                removeSearchBar.visibility = View.GONE
//            }
//
//            settingsDots.setOnClickListener {
//                it.applyScaleAnimation()
//                showCustomLayout()
//            }
//
//            close.apply {
//                visibility =
//                    if (selectable) View.VISIBLE else View.GONE
//                setOnClickListener {
//                    it.applyScaleAnimation()
//                    exerciseViewModel.onConfirm()
//                    findNavController().navigateUp()
//                    //R.id.exercises_to_create_workout_template
//                }
//            }

            confirm.apply {
                visibility = if (selectable || replaceable || addable) View.VISIBLE else View.GONE
                setOnClickListener {
                    it.applyScaleAnimation()
                    exerciseViewModel.confirmSelectedExercises()
                    findNavController().navigateUp()
                }
            }

//            settingsFilters.setOnClickListener {
//                it.applyScaleAnimation()
//                FilterDialog().show(childFragmentManager, FilterDialog.TAG)
//            }
        }
    }

    private fun showExerciseMenu(clickedView: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(context, R.style.MyPopUp), clickedView)
        popupMenu.menuInflater.inflate(R.menu.menu_add_exercise, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.go_to_add_exercise -> {
                    findNavController().navigate(R.id.exercise_to_create_exercise)
                    true
                }

                else -> false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        exerciseViewModel.onConfirm()
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