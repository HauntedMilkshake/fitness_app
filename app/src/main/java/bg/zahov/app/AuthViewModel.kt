package bg.zahov.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

//FIXME This VM does a lot. I will add a bigger comment here and refer to this class in other places
// where the comments are relevant.
// 1. I see that you use FirebaseAuth all over the project. This is not a good idea - what if you
// decide to switch to another identity provider? A common approach is to define an authentication
// service (not an Android Service) interface with methods for login, sign-up, logout, account deletion and so on and use
// that when these functionalities are needed (for this project in the login/signup and settings
// feature packages). Once user access is granted, the auth service should update the user in UserRepository
// with the current user - this should be the only component that updates the values in UserRepository.
// All other application components should only care about the current user provided by UserRepository.
// A good practice is to use a sealed class for the User entity that has a NoUser subclass so you always
// have a non-nullable user in the app.
// 2. This is not a good place and way to handle periodic data sync. Check out WorkManager APIs https://developer.android.com/develop/background-work/background-tasks/persistent
class AuthViewModel : ViewModel() {

}
