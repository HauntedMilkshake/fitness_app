package bg.zahov.app.ui.history.info

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
import bg.zahov.app.showBottomNav
import bg.zahov.fitness.app.R

class HistoryInfoFragment : Fragment() {

    private val historyInfoViewModel: HistoryInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        activity?.hideBottomNav()
        setupTopBar()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HistoryInfoScreen()
            }
        }
    }

    private fun setupTopBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_history_info, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }

                    R.id.delete -> {
                        historyInfoViewModel.delete()
                        findNavController().navigateUp()
                        true
                    }

                    R.id.save_as_workout_template -> {
                        historyInfoViewModel.saveAsTemplate()
                        true
                    }

                    else -> false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        activity?.showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        historyInfoViewModel.state.removeObservers(viewLifecycleOwner)
    }
}