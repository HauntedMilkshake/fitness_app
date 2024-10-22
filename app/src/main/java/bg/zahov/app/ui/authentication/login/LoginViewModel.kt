package bg.zahov.app.ui.authentication.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getUserProvider
import bg.zahov.app.ui.authentication.AuthenticationState
import bg.zahov.app.ui.authentication.UiInfo
import bg.zahov.app.util.isEmail
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.IllegalArgumentException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val info: UiInfo = UiInfo()

    fun getInfoMail() = info.mail
    fun getInfoPassword() = info.password
    fun getInfoPasswordVisibility() = info.passwordVisibility

    fun changePasswordVisibility() {
        info.passwordVisibility = !info.passwordVisibility
        notifyChange()
    }

    fun setInfo(
        mail: String? = null,
        password: String? = null,
    ) {
        mail?.let { info.mail = it }
        password?.let { info.password = it }
        notifyChange()

    }
    private var counter: Int = 0

    private val _uiState = MutableStateFlow<AuthenticationState>(AuthenticationState.Default(info, counter))
    val uiState: StateFlow<AuthenticationState> = _uiState.asStateFlow()


    private val auth by lazy {
        application.getUserProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }

    fun login(email: String = info.mail, password: String = info.password) {
        if (email.isEmpty() || password.isEmpty()) {
            notifyChange("Don't leave empty fields")
            return
        }

        if (!email.isEmail()) {
            notifyChange("Email not valid")
            return
        }

        viewModelScope.launch {
            try {
                auth.login(email, password)
                    .addOnSuccessListener {
                        _uiState.value = AuthenticationState.Authenticate
                    }
                    .addOnFailureListener {
                        it.message?.let { it1 -> notifyChange(it1) }
                    }
            } catch (e: IllegalArgumentException) {
                serviceError.initiateCountdown()
            }
        }
    }

    fun sendPasswordResetEmail(email: String = info.mail) {
        if (email.isEmpty()) {
            notifyChange("Email must not be empty")
            return
        }

        if (!email.isEmail()) {
            notifyChange("Email is not valid")
            return
        }

        viewModelScope.launch {
            auth.passwordResetByEmail(email)
                .addOnSuccessListener { notifyChange("Rest password email has been sent") }
                .addOnFailureListener { notifyChange("Couldn't send reset password email!") }
        }
    }

    private fun notifyChange(text: String? = null) {
        counter++
        _uiState.value = text?.let {
            AuthenticationState.Notify(
                uiInfo = info,
                message = it,
                stateCounter = counter
                )
        } ?: AuthenticationState.Default(info, counter)
    }
}

