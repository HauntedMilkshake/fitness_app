//package bg.zahov.app.ui.error
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import bg.zahov.app.hideBottomNav
//import bg.zahov.app.hideTopBar
//
////class FragmentShuttingDown : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
//    ): View {
//        requireActivity().hideTopBar()
//        requireActivity().hideBottomNav()
//        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                ShuttingDownScreen()
//            }
//        }
//    }
//}