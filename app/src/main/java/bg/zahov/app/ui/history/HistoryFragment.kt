package bg.zahov.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R

class HistoryFragment : Fragment() {
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        activity?.showTopBar()
        activity?.showBottomNav()
        setupTopBar()
        return ComposeView(requireContext()).apply {
            setContent {
                HistoryScreen(historyViewModel) {
                    findNavController().navigate(
                        R.id.history_to_history_info
                    )
                }
            }
        }
    }

    private fun setupTopBar() {
        activity?.setToolBarTitle(R.string.history)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_history, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.calendar -> {
                        findNavController().navigate(R.id.history_to_calendar)
                        true
                    }

                    else -> false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        activity?.showTopBar()
        activity?.showBottomNav()
    }
}