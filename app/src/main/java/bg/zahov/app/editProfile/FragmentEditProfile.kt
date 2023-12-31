package bg.zahov.app.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import bg.zahov.fitness.app.databinding.FragmentEditProfileBinding

class FragmentEditProfile: Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            editProfileViewModel.userName.observe(viewLifecycleOwner){
                usernameFieldText.apply {
                    setText(it)
                }
            }
            editProfileViewModel.userEmail.observe(viewLifecycleOwner){
                emailFieldText.apply {
                    setText(it)
                }
            }
            saveChanges.setOnClickListener {
                if(usernameFieldText.text.isNullOrEmpty() || emailFieldText.text.isNullOrEmpty() || isEmailNotValid(emailFieldText.text.toString()) ){
                    Toast.makeText(requireContext(), "Cannot have empty fields", Toast.LENGTH_SHORT).show()
                }else{
                    editProfileViewModel.changeUserName(usernameFieldText.text.toString())
                    editProfileViewModel.changeEmail(emailFieldText.text.toString())
                }
            }
        }
    }
    private fun isEmailNotValid(email: String) = !Regex("^\\S+@\\S+\\.\\S+$").matches(email)
}