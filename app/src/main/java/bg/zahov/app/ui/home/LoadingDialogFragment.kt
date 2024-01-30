package bg.zahov.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import bg.zahov.app.data.model.HomeUiMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.app.showBottomNav
import bg.zahov.fitness.app.databinding.DialogFragmentLoadingBinding
import com.google.android.material.imageview.ShapeableImageView

class LoadingDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentLoadingBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFragmentLoadingBinding.inflate(inflater, container, false)
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

            homeViewModel.state.map { HomeUiMapper.map(it) }.observe(viewLifecycleOwner) {
                if(!it.isLoading) dismiss()
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
        requireActivity().showBottomNav()
        _binding = null
    }

    companion object {
        const val TAG = "LoadingDialogFragment"
    }
}
