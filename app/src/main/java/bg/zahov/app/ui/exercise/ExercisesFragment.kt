package bg.zahov.app.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R

class ExercisesFragment : Fragment() {
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    private val state by lazy {
        arguments?.getString(STATE_ARG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        exerciseViewModel.updateFlag(state)
        return ComposeView(requireContext()).apply {
            activity?.apply {
                showTopBar()
                if (state != null) hideBottomNav() else showBottomNav()
                (this as? AppCompatActivity)?.setSupportActionBar(findViewById(R.id.toolbar))
                (this as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(state != null)
                (this as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
                addMenuProvider(object : MenuProvider {
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
                                            query?.let { }
                                            return true
                                        }

                                        override fun onQueryTextChange(query: String?): Boolean {
                                            query?.let { name ->
                                                exerciseViewModel.onSearchChange(name)
                                            }
                                            return true
                                        }
                                    })
                                }

                                true
                            }

                            R.id.filter -> {
                                exerciseViewModel.updateShowDialog(true)
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

                setToolBarTitle(
                    when (state) {
                        ADD_EXERCISE_ARG -> {
                            R.string.add_exercise
                        }

                        SELECT_EXERCISE_ARG -> {
                            R.string.add_exercise
                        }

                        REPLACE_EXERCISE_ARG -> {
                            R.string.replace_exercise
                        }

                        else -> {
                            R.string.exercise
                        }
                    }
                )
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    ExercisesScreen(navigateInfo = {
                        findNavController().navigate(R.id.exercises_to_exercise_info_navigation)
                    }, navigateBack = {
                        findNavController().popBackStack()
                    })
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {}
    override fun onPause() {
        super.onPause()
        exerciseViewModel.resetExerciseSelection()
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.setSupportActionBar(activity?.findViewById(R.id.toolbar))
        if (state != null) activity?.hideBottomNav() else activity?.showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.invalidateOptionsMenu()
    }

    companion object {
        const val REPLACE_EXERCISE_ARG = "REPLACING"
        const val ADD_EXERCISE_ARG = "SELECTING"
        const val SELECT_EXERCISE_ARG = "ADDING"
        const val STATE_ARG = "State"
    }
}