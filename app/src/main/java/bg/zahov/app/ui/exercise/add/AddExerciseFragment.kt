//package bg.zahov.app.ui.exercise.add
//
//import android.os.Bundle
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
//import androidx.navigation.fragment.findNavController
//import bg.zahov.app.hideBottomNav
//import bg.zahov.app.setToolBarTitle
//import bg.zahov.fitness.app.R
//
//class AddExerciseFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        activity?.hideBottomNav()
//        activity?.setToolBarTitle(R.string.add_exercise)
//        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
//        activity?.addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menu.clear()
//                menuInflater.inflate(R.menu.menu_toolbar_new_exercise, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when (menuItem.itemId) {
//                    R.id.home -> {
//                        findNavController().popBackStack()
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//        })
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                AddExerciseScreen(navigate = { findNavController().navigate(R.id.add_exercise_to_exercises) })
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        activity?.hideBottomNav()
//    }
//}