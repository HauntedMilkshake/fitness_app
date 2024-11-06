package bg.zahov.app.ui.workout.start

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
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R

class StartWorkoutFragment : Fragment() {

    private val startWorkoutViewModel: StartWorkoutViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        setUpTopBar()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                StartWorkoutScreen(startWorkoutViewModel,
                    onWorkoutClick = {
                        findNavController().navigate(
                            R.id.start_workout_to_template_workout_info,
                            bundleOf(WORKOUT_ID_ARG_KEY to it)
                        )
                    },
                    onAddTemplateWorkout = { findNavController().navigate(R.id.workout_to_create_workout_template) },
                    onEditWorkout = {
                        findNavController().navigate(
                            R.id.workout_to_create_workout_template,
                            bundleOf(EDIT_FLAG to true, WORKOUT_ID_ARG_KEY to it)
                        )
                    })
            }
        }
    }

    fun setUpTopBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity?.setToolBarTitle(R.string.workout)
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_start_workout, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        })
    }

    override fun onResume() {
        super.onResume()
        activity?.showTopBar()
        activity?.showBottomNav()
    }

    companion object {
        const val WORKOUT_ID_ARG_KEY = "WORKOUT_ID"
        const val EDIT_FLAG = "EDIT"
    }
}