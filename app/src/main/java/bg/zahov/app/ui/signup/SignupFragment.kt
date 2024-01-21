package bg.zahov.app.ui.signup

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.SignupUiMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentSignUpBinding

//FIXME clear _binding...
class SignupFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val signupViewModel: SignupViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            usernameFieldText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    emailFieldText.requestFocus()
                    true
                } else {
                    false
                }
            }

            emailFieldText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    passwordFieldText.requestFocus()
                    true
                } else {
                    false
                }
            }

            passwordFieldText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    confirmPasswordFieldText.requestFocus()
                    true
                } else {
                    false
                }
            }

            confirmPasswordFieldText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    registerButton.callOnClick()
                    true
                } else {
                    false
                }
            }

            registerButton.setOnClickListener {
                signupViewModel.signUp(usernameFieldText.text.toString(), emailFieldText.text.toString(), passwordFieldText.text.toString(), confirmPasswordFieldText.text.toString())
            }

            //messages might not be propagated
            signupViewModel.state.map { SignupUiMapper.map(it) }.observe(viewLifecycleOwner) {
                if (it.authenticated) {
                    findNavController().navigate(R.id.loading_to_home)
                } else {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            alreadyHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.signup_to_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}