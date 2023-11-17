package bg.zahov.app.welcome

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentWelcomeBinding

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
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            entry.setOnClickListener {
                if(welcomeText.visibility != View.VISIBLE) {
                    it.startAnimation(
                        ScaleAnimation(
                            0f,
                            20f,
                            0f,
                            20f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f
                        ).apply { duration = 1500 })
                    it.postDelayed({
                        welcomeText.visibility = View.VISIBLE
                        underWelcomeText.visibility = View.VISIBLE
                        registerButton.visibility = View.VISIBLE
                        logInButton.visibility = View.VISIBLE
                        //todo fix background color
                        root.setBackgroundColor(R.color.background)
                    }, 1500)
                }
            }
            logInButton.setOnClickListener{
                findNavController().navigate(R.id.welcome_to_login)
            }
            registerButton.setOnClickListener{
                findNavController().navigate(R.id.welcome_to_signup)
            }
        }
    }

}