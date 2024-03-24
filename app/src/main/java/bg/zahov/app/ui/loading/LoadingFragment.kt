package bg.zahov.app.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.state.LoadingUiMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.databinding.FragmentLoadingBinding
import com.google.android.material.imageview.ShapeableImageView

class LoadingFragment : Fragment() {
    private var _binding: FragmentLoadingBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val loadingViewModel: LoadingViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            animateView(bottomLeft)
            animateView(bottomRight)
            animateView(topLeft)
            animateView(topRight)
            loadingViewModel.onAppStart()

            loadingViewModel.state.map { LoadingUiMapper.map(it) }.observe(viewLifecycleOwner) {
                it.message?.let { message ->
                    //TODO(end process)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                it.destination?.let { des ->
                    findNavController().navigate(des)
                }
            }
        }
    }

    private fun animateView(view: ShapeableImageView) {
        val scaleAnimation = ScaleAnimation(
            0f, 10f,
            0f, 10f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        scaleAnimation.duration = 2000L
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        view.startAnimation(scaleAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
