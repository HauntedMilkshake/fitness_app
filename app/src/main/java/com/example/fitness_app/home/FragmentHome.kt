package com.example.fitness_app.profile

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitness_app.R
import com.example.fitness_app.databinding.FragmentHomeBinding
import com.example.fitness_app.showBottomNav
import com.google.android.material.appbar.AppBarLayout

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var allowBackPressed: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().showBottomNav()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().popBackStack(findNavController().currentDestination!!.id, false)
        //TODO fix it so we can actually pop the backstack once we are logged
//        binding.apply {
//            topBar.apply {
//                addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener() { _, verticalOffset ->
//                    profileText.setTextSize(TypedValue.COMPLEX_UNIT_SP, resources.getDimension(R.dimen.original_text_size) + ( (resources.getDimension(R.dimen.enlarged_text_size) - resources.getDimension(R.dimen.original_text_size) ) * -verticalOffset / totalScrollRange.toFloat()))
//                    elevation = if(verticalOffset == 0){
//                        4f
//                    }else{
//                        0f
//                    }
//                })
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    override fun onResume() {
        super.onResume()
        allowBackPressed = true
    }
}
