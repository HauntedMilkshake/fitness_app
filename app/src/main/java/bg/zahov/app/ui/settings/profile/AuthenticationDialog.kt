package bg.zahov.app.ui.settings.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import bg.zahov.app.data.model.state.EditProfileUiMapper
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.databinding.DialogFragmentAuthenticationBinding

class AuthenticationDialog : DialogFragment() {

    private var _binding: DialogFragmentAuthenticationBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val editProfileViewModel: EditProfileViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cancel.setOnClickListener {
                it.applyScaleAnimation()
                dismiss()
            }

            confirm.setOnClickListener {
                it.applyScaleAnimation()
                editProfileViewModel.unlockFields(passwordFieldText.text.toString())
            }

            editProfileViewModel.isUnlocked.observe(viewLifecycleOwner) {
                if (it) dismiss()
            }

            editProfileViewModel.state.map { EditProfileUiMapper.map(it) }.observe(viewLifecycleOwner) {
                showToast(it.notifyMessage)
            }
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AuthenticateBeforeSensitiveInfo"
    }
}
