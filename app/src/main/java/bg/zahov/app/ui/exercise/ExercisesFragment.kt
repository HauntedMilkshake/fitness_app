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

    private val selectable by lazy {
        arguments?.getBoolean("SELECTABLE") ?: false
    }

    private val replaceable by lazy {
        arguments?.getBoolean("REPLACING") ?: false
    }

    private val addable by lazy {
        arguments?.getBoolean("ADDABLE") ?: false
    }
    private var showDialog: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            exerciseViewModel.updateFlag(
                replaceable = replaceable,
                selectable = selectable,
                addable = addable
            )
            requireActivity().showTopBar()
            if (addable || selectable || replaceable) requireActivity().hideBottomNav() else requireActivity().showBottomNav()
            (activity as? AppCompatActivity)?.setSupportActionBar(activity?.findViewById(R.id.toolbar))
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
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ExercisesScreen()
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
        (activity as? AppCompatActivity)?.setSupportActionBar(activity?.findViewById(R.id.toolbar))
        if (addable || selectable || replaceable) requireActivity().hideBottomNav() else requireActivity().showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().invalidateOptionsMenu()
    }
}