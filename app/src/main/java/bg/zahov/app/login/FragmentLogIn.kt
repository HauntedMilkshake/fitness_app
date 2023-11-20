package bg.zahov.app.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentLogInBinding

class FragmentLogIn: Fragment() {
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        requireActivity().hideBottomNav()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            forgotPassword.setOnClickListener {
                val email = emailFieldText.text.toString()
                if(isEmailNotValid(email)){
                    Toast.makeText(requireContext(), "Email not valid", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                loginViewModel.sendPasswordResetEmail(email){success, errorMessage ->
                    if(success){
                        //todo
                    }else{
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            logInButton.setOnClickListener {

                val password = passwordFieldText.text.toString()
                val email = emailFieldText.text.toString()

                if(areFieldsEmpty(email, password)){
                    Toast.makeText(requireContext(), "Fields can't be empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(isEmailNotValid(email)){
                    Toast.makeText(requireContext(), "Email not valid", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                loginViewModel.login(email, password) { success, errorMessage ->
                    Log.d("goingToHome", "WE ARE HERE ${success.toString()}")
                    if(success){
                        findNavController().navigate(R.id.login_to_home)
                    }else{
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            createAccount.setOnClickListener {
                findNavController().navigate(R.id.login_to_signup)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun areFieldsEmpty(email: String, password: String) = listOf(email, password).count { it.isNullOrEmpty() } >= 1
    private fun isEmailNotValid(email: String) = !Regex("^\\S+@\\S+\\.\\S+$").matches(email)
}