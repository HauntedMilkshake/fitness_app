package bg.zahov.app.ui.settings.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.EditProfileUiMapper
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentEditProfileBinding

//FIXME _binding should be set to null in onDestroyView() to prevent memory leaks
class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels({ requireActivity() })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            back.setOnClickListener {
                findNavController().navigate(R.id.edit_profile_to_settings)
            }

            resetPassword.setOnClickListener {
                editProfileViewModel.sendPasswordResetLink()
            }
            editProfileViewModel.state.map { EditProfileUiMapper.map(it) }.observe(viewLifecycleOwner) {
                usernameFieldText.setText(it.username)
                emailFieldText.setText(it.email)

                lock.setImageResource(if(it.isUnlocked) R.drawable.ic_open_lock else R.drawable.ic_closed_lock)
                usernameField.isEnabled = it.isUnlocked
                emailField.isEnabled = it.isUnlocked

                if(!it.errorMessage.isNullOrEmpty()) {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                if(!it.notifyMessage.isNullOrEmpty()) {
                    Toast.makeText(context, it.notifyMessage, Toast.LENGTH_SHORT).show()
                }

                if(it.isUnlocked) {
                    saveChanges.setOnClickListener {
                        editProfileViewModel.updateEmail(emailFieldText.toString())
                        editProfileViewModel.updateUsername(usernameFieldText.toString())
                        editProfileViewModel.updatePassword(passwordFieldText.toString())
                    }
                }
            }

            lock.setOnClickListener {
                AuthenticationDialog().show(childFragmentManager, AuthenticationDialog.TAG)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}