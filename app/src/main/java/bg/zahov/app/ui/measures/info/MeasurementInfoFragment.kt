package bg.zahov.app.ui.measures.info

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
import bg.zahov.app.showTopBar
import bg.zahov.app.ui.measures.MeasuresFragment.Companion.MEASUREMENT_ARGS
import bg.zahov.fitness.app.R

class MeasurementInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val title = arguments?.getString(MEASUREMENT_ARGS) ?: "Measurement"
        val measurementInputViewModel: MeasurementInfoViewModel by viewModels()
        measurementInputViewModel.updateTitle(title)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        activity?.setToolBarTitle(title)
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_measures, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                R.id.home -> {
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }, viewLifecycleOwner)
        activity?.showTopBar()
        activity?.hideBottomNav()
        return ComposeView(requireContext()).apply {
            activity?.hideBottomNav()
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MeasurementInfoScreen()
            }
        }
    }
}