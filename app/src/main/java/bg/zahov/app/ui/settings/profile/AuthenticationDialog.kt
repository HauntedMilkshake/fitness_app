package bg.zahov.app.ui.settings.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import bg.zahov.fitness.app.databinding.DialogFragmentAuthenticationBinding

class AuthenticationDialog : DialogFragment() {
    //FIXME since the binding is used only in onCreateDialog, you can
    private var _binding: DialogFragmentAuthenticationBinding? = null
    //FIXME code style - get() method definition is usually placed on a new line with indent
    // also use requireNotNull() instead of !!
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels({ requireActivity() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFragmentAuthenticationBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
        //FIXME you can control dialog styling from the application theme or another theme declared in
        // themes.xml
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.Transparent.toArgb()))
        dialog.setOnShowListener {
            binding.cancel.setOnClickListener {
                dismiss()
            }

            binding.confirm.setOnClickListener {
//                editProfileViewModel.unlockFields(binding.passwordFieldText.text.toString()) { isSuccess, message ->
//                    if (isSuccess) {
//                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//                        dismiss()
//                    } else {
//                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//                    }
//                }
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "AuthenticateBeforeSensitiveInfo"
    }
}
