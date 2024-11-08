package bg.zahov.app.ui.settings.profile

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
import androidx.navigation.fragment.findNavController
import bg.zahov.app.setToolBarTitle
import bg.zahov.fitness.app.R

class EditProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
            requireActivity().setToolBarTitle(R.string.edit_profile_text)
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.menu_toolbar_edit_profile, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                    R.id.home -> {
                        findNavController().popBackStack()
                    }

                    else -> false
                }
            })
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EditProfileScreen()
            }
        }
    }
}