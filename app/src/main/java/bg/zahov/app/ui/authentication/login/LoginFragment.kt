package bg.zahov.app.ui.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.fitness.app.R

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoginScreen(
                    onNavigateToSignUp = { findNavController().navigate(R.id.login_to_signup) })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().hideBottomNav()
        requireActivity().hideTopBar()
    }
}