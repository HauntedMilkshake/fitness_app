package bg.zahov.app.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    fun logout(){
        auth.signOut()
    }
}