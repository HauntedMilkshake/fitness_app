package bg.zahov.app.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R

class LoadingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoadingScreen(
                    navigateWelcome = {
                        findNavController().navigate(R.id.loading_to_welcome)
                    },
                    navigateHome = {
                        findNavController().navigate(R.id.loading_to_home)
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.hideBottomNav()
    }
}