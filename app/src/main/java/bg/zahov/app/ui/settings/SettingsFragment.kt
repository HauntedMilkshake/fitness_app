//package bg.zahov.app.ui.settings
//
//import android.os.Bundle
//import android.transition.TransitionInflater
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.core.view.MenuProvider
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import bg.zahov.app.hideBottomNav
//import bg.zahov.app.setToolBarTitle
//import bg.zahov.app.ui.exercise.info.ExerciseInfoScreen
//import bg.zahov.fitness.app.R
//
//class SettingsFragment : Fragment() {
//    private val settingsViewModel: SettingsViewModel by viewModels()
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
//        requireActivity().setToolBarTitle(R.string.settings_text)
//        requireActivity().addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menu.clear()
//                menuInflater.inflate(R.menu.menu_toolbar_settings, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when (menuItem.itemId) {
//                    R.id.home -> {
//                        findNavController().navigate(R.id.settings_to_home)
//                        true
//                    }
//
//                    R.id.reset -> {
//                        settingsViewModel.resetSettings()
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//        })
//        return ComposeView(requireContext()).apply {
//            requireActivity().hideBottomNav()
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                SettingsScreen(
//                    settingsViewModel,
//                    navigateBack = { findNavController().navigate(R.id.settings_to_welcome) },
//                    navigateEditProfile = { findNavController().navigate(R.id.settings_to_edit_profile) })
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enterTransition =
//            TransitionInflater.from(requireContext()).inflateTransition(R.transition.slide_up)
//        exitTransition =
//            TransitionInflater.from(requireContext()).inflateTransition(R.transition.fade_out)
//    }
//}