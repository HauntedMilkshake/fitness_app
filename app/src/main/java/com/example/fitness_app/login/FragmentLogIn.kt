package com.example.fitness_app.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitness_app.databinding.FragmentLogInBinding

class FragmentLogIn: Fragment() {
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //for hiding the navbar, haven't tested it
//        val view = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
//
//        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
//        fab.visibility = View.GONE
//        view.visibility = View.GONE

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}