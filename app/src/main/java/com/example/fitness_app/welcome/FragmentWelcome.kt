package com.example.fitness_app.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitness_app.R
import com.example.fitness_app.databinding.FragmentWelcomeBinding
import com.example.fitness_app.hideBottomNav
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentWelcome: Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        requireActivity().hideBottomNav()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            logInButton.setOnClickListener{
                findNavController().navigate(R.id.welcome_to_login)
            }
            registerButton.setOnClickListener{
                findNavController().navigate(R.id.welcome_to_signup)
            }
        }
    }

}