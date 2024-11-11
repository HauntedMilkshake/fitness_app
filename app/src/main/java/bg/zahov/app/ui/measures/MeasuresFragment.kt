package bg.zahov.app.ui.measures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R

class MeasuresFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().setToolBarTitle(R.string.measure)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_measures, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        })
        requireActivity().showTopBar()
        return ComposeView(requireContext()).apply {
            setContent {
                MeasuresScreen(navigateInfo = {
                    findNavController().navigate(
                        R.id.measures_to_measurement_info,
                        bundleOf(MEASUREMENT_ARGS to it)
                    )

                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().showBottomNav()
    }

    companion object {
        const val MEASUREMENT_ARGS = "measurement name"
    }
}