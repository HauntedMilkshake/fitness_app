package bg.zahov.app.ui.settings.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.state.EditProfileUiMapper
import bg.zahov.app.setToolBarTitle
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
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        requireActivity().setToolBarTitle(R.string.edit_profile_text)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_edit_profile, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                R.id.home -> {
                    findNavController().popBackStack()
                }

                else -> false
            }
        })
        binding.apply {
            resetPassword.setOnClickListener {
                editProfileViewModel.sendPasswordResetLink()
            }

            editProfileViewModel.name.observe(viewLifecycleOwner) {
                usernameFieldText.setText(it)
            }

            editProfileViewModel.isUnlocked.observe(viewLifecycleOwner) {
                lock.setImageResource(if (it) R.drawable.ic_open_lock else R.drawable.ic_closed_lock)
                passwordField.isEnabled = it
                usernameField.isEnabled = it

                if (it) {
                    saveChanges.setOnClickListener {
                        editProfileViewModel.updateUsername(usernameFieldText.text.toString())
                        editProfileViewModel.updatePassword(passwordFieldText.text.toString())
                    }
                }
            }

            editProfileViewModel.state.map { EditProfileUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
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
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}