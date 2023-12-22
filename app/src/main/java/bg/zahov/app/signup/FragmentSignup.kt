package bg.zahov.app.signup

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentSignUpBinding

class FragmentSignup : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: SignupViewModel by viewModels()
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
                if( keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN ){
                    emailFieldText.requestFocus()
                    true
                }else{
                    false
                }
            }

            emailFieldText.setOnKeyListener { _, keyCode, event ->
                if( keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN ){
                    passwordFieldText.requestFocus()
                    true
                }else{
                    false
                }
            }

            passwordFieldText.setOnKeyListener{ _, keyCode, event ->
                if( keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN ){
                    confirmPasswordFieldText.requestFocus()
                    true
                }else{
                    false
                }
            }

            confirmPasswordFieldText.setOnKeyListener{ _, keyCode, event ->
                if( keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN ){
                   registerButton.callOnClick()
                    true
                }else{
                    false
                }
            }

            registerButton.setOnClickListener {
                val password = passwordFieldText.text.toString()
                val confirmPassword = confirmPasswordFieldText.text.toString()
                val email = emailFieldText.text.toString()
                val username = usernameFieldText.text.toString()

                if (areFieldsEmpty(username, email, password)) {
                    Toast.makeText(
                        requireContext(),
                        "All fields must not be empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (checkPasswords(password, confirmPassword)) {
                    Toast.makeText(requireContext(), "Passwords must match!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                if (isEmailNotValid(email)) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter a valid email address",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                authViewModel.signUp(username, email, password) { success, errorMessage ->
                    if (success) {
                        findNavController().navigate(R.id.signup_to_home)
                    } else {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            alreadyHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.signup_to_login)
            }
        }
    }

    private fun checkPasswords(firstPass: String, secondPass: String) = firstPass != secondPass
    private fun areFieldsEmpty(userName: String?, email: String?, pass: String?) =
        listOf(userName, email, pass).count { it.isNullOrEmpty() } >= 1

    private fun isEmailNotValid(email: String) = !Regex("^\\S+@\\S+\\.\\S+$").matches(email)
}