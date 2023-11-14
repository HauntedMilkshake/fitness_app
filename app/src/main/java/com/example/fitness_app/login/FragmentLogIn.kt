package com.example.fitness_app.login

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
import com.example.fitness_app.R
import com.example.fitness_app.databinding.FragmentLogInBinding
import com.example.fitness_app.hideBottomNav

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
                    if(success){
                        //should be fixed because this doesn't cover the case where the user clicks on already have an account and we need to input the
                        //username when an account is first created then we need to query it here just in case
                        //might be an option to do it in the profile viewmodel as well
                        findNavController().navigate(R.id.login_to_home, bundleOf("user-name" to arguments?.getString("user-name")))
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
    fun areFieldsEmpty(email: String, password: String) = listOf(email, password).count { it.isNullOrEmpty() } >= 1
    fun isEmailNotValid(email: String) = !Regex("^\\S+@\\S+\\.\\S+$").matches(email)
}