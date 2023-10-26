package com.example.fitness_app.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitness_app.R
import com.example.fitness_app.databinding.FragmentSignUpBinding
import com.example.fitness_app.hideBottomNav

class FragmentSignup : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            registerButton.setOnClickListener {
                findNavController().navigate(R.id.signup_to_login)
            }
            alreadyHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.signup_to_login)
            }
        }
    }

}