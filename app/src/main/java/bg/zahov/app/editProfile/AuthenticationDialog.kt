package bg.zahov.app.editProfile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.DialogFragmentAuthenticationBinding

class AuthenticationDialog : DialogFragment() {
    private var _binding: DialogFragmentAuthenticationBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels({requireActivity()})

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFragmentAuthenticationBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()

        dialog.setOnShowListener {
            binding.cancel.setOnClickListener {
                dismiss()
            }

            binding.confirm.setOnClickListener {
                editProfileViewModel.unlockFields(binding.passwordFieldText.text.toString()) {
                    if(it){
                        Toast.makeText(requireContext(), "Successfully logged in", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }else{
                        Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "AuthenticateBeforeSensitiveInfo"
    }
}
