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

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding
        get() = requireNotNull(_binding)
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

            editProfileViewModel.name.observe(viewLifecycleOwner) {
                usernameFieldText.setText(it)
            }

            editProfileViewModel.email.observe(viewLifecycleOwner) {
                emailFieldText.setText(it)
            }

            editProfileViewModel.isUnlocked.observe(viewLifecycleOwner) {
                lock.setImageResource(if (it) R.drawable.ic_open_lock else R.drawable.ic_closed_lock)
                usernameField.isEnabled = it
                emailField.isEnabled = it

                if (it) {
                    saveChanges.setOnClickListener {
                        editProfileViewModel.updateEmail(emailFieldText.toString())
                        editProfileViewModel.updateUsername(usernameFieldText.toString())
                        editProfileViewModel.updatePassword(passwordFieldText.toString())
                    }
                }
            }

            editProfileViewModel.state.map { EditProfileUiMapper.map(it) }
                .observe(viewLifecycleOwner) {

                    showToast(it.errorMessage)
                    showToast(it.notifyMessage)
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

    private fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}