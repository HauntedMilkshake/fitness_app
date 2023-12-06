package bg.zahov.app.loading

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentLoadingBinding
import com.google.android.material.imageview.ShapeableImageView

class FragmentLoading : Fragment() {
    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!
    private var isNavigationInProgress = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("Current location", findNavController().currentDestination.toString())
        requireActivity().hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            animateView(bottomLeft)
            animateView(bottomRight)
            animateView(topLeft)
            animateView(topRight)
        }
    }

    private fun animateView(view: ShapeableImageView) {
        val scaleAnimation = ScaleAnimation(
            0f, 10f,
            0f, 10f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        scaleAnimation.duration = 1000L
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                if(!isNavigationInProgress){
                    isNavigationInProgress = true
                    findNavController().navigate(R.id.loading_to_home)

                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        view.startAnimation(scaleAnimation)
    }
}
//TODO(Make the animation load based on whether we have created a realm file for the user instead of hardcoded value)
