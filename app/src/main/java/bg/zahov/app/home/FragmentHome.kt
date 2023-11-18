package bg.zahov.app.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.showBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
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
        binding.apply {
            settings.setOnClickListener {
                findNavController().navigate(R.id.home_to_settings)
            }
            homeViewModel.userName.observe(viewLifecycleOwner){
                profileName.text = it
            }
            homeViewModel.userWorkouts.observe(viewLifecycleOwner){
                numberOfWorkouts.text = it.toString()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        allowBackPressed = true
    }
}
