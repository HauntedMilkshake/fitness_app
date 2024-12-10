//package bg.zahov.app.ui.home
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import bg.zahov.app.hideTopBar
//import bg.zahov.app.showBottomNav
//import bg.zahov.fitness.app.R
//
//class HomeFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
//    ): View {
//        activity?.showBottomNav()
//        activity?.hideTopBar()
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                HomeScreen{ findNavController().navigate(R.id.home_to_settings) }
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        activity?.showBottomNav()
//    }
//}