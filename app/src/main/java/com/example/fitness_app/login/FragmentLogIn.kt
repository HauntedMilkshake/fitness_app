package com.example.fitness_app.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitness_app.R
import com.example.fitness_app.databinding.FragmentLogInBinding
import com.example.fitness_app.hideBottomNav

class FragmentLogIn: Fragment() {
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
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
                // TODO: forgot password xd
            }
            logInButton.setOnClickListener {
                findNavController().navigate(R.id.login_to_home)
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
}