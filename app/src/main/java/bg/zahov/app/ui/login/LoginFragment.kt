package bg.zahov.app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.fitness.app.databinding.FragmentLogInBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoginScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().hideBottomNav()
        requireActivity().hideTopBar()
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.apply {
//
//            emailFieldText.setOnKeyListener { _, keyCode, event ->
//                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
//                    passwordFieldText.requestFocus()
//                } else {
//                    false
//                }
//            }
//
//            forgotPassword.setOnClickListener {
//                loginViewModel.sendPasswordResetEmail(emailFieldText.text.toString())
//            }
//
//            logInButton.setOnClickListener {
//                loginViewModel.login(
//                    emailFieldText.text.toString(),
//                    passwordFieldText.text.toString()
//                )
//            }
//
//            loginViewModel.state.map { LoginUiMapper.map(it) }.observe(viewLifecycleOwner) {
//                if (it.isAuthenticated) {
//                    findNavController().navigate(R.id.login_to_loading)
//                }
//                showToast(it.notifyMessage)
//                showToast(it.errorMessage)
//            }
//
//            createAccount.setOnClickListener {
//                findNavController().navigate(R.id.login_to_signup)
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun showToast(message: String?) {
//        message?.let {
//            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//        }
//    }
}