package bg.zahov.app.ui.measures

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showTopBar
import bg.zahov.app.ui.exercise.filter.FilterDialog
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentMeasuresBinding

class MeasuresFragment : Fragment() {
    private var _binding: FragmentMeasuresBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeasuresBinding.inflate(inflater, container, false)
        requireActivity().showTopBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setToolBarTitle(R.string.measure)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_measures, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = false
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}