package bg.zahov.app.ui.authentication.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.fitness.app.R

class SignupFragment : Fragment() {
    private val signupViewModel: SignupViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SignupScreen(signupViewModel,
                    onNavigateToLogin = {
                        findNavController().navigate(R.id.signup_to_login)
                    },
                    onAuthenticate = { findNavController().navigate(R.id.signup_to_loading) })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.hideBottomNav()
        activity?.hideTopBar()
    }
}