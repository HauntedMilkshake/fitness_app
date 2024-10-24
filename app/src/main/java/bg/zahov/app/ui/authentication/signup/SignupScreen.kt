package bg.zahov.app.ui.authentication.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.ui.custom.CommonPasswordField
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.app.util.isEmail
import bg.zahov.fitness.app.R

@Composable
fun SignupScreen(
    signupViewModel: SignupViewModel = viewModel(),
    onAuthenticate: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val uiState = signupViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.value.isUserAuthenticated) {
        LaunchedEffect(Unit) {
            onAuthenticate()
        }
    }

    uiState.value.notifyUser?.let {
        LaunchedEffect(Unit) {
            showToast(it, context)
            signupViewModel.messageShown()
        }
    }

    SignupContent(
        onNameChange = { signupViewModel.onUsernameChange(it) },
        onEmailChange = { signupViewModel.onEmailChange(it) },
        onPasswordChange = { signupViewModel.onPasswordChange(it) },
        onConfirmPasswordChange = { signupViewModel.onConfirmPasswordChange(it) },
        onSignupButtonPressed = { signupViewModel.signUp() },
        onPasswordVisibilityChange = { signupViewModel.onPasswordVisibilityChange(it) },
        username = uiState.value.username,
        email = uiState.value.email,
        password = uiState.value.password,
        confirmPassword = uiState.value.confirmPassword,
        showPassword = uiState.value.passwordVisibility,
        onNavigateToLogin = onNavigateToLogin

    )
}

@Composable
fun SignupContent(
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignupButtonPressed: () -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    username: String,
    email: String,
    password: String,
    confirmPassword: String,
    showPassword: Boolean,
    onNavigateToLogin: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 156.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 20.dp),
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CommonTextField(
                text = username,
                label = { Text(stringResource(R.string.username_text)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_profile), null) },
                onTextChange = {
                    onNameChange(it)
                })
            CommonTextField(
                text = email,
                label = { Text(text = stringResource(R.string.email_text_field_hint)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_email), null) },
                onTextChange = {
                    onEmailChange(it)
                },
                isEmail = true
            )
            CommonPasswordField(
                password = password,
                passwordVisible = showPassword,
                label = { Text(stringResource(R.string.password_text_field_hint)) },
                onPasswordChange = {
                    onPasswordChange(it)
                },
                onPasswordVisibilityChange = {
                    onPasswordVisibilityChange(it)
                })
            CommonPasswordField(
                password = confirmPassword,
                passwordVisible = showPassword,
                label = { Text(stringResource(R.string.confirm_password_text)) },
                onPasswordChange = {
                    onConfirmPasswordChange(it)
                },
                onPasswordVisibilityChange = {
                    onPasswordVisibilityChange(it)
                })
        }

        Button(
            modifier = Modifier
                .padding(top = 20.dp)
                .width(240.dp),
            colors = ButtonColors(
                containerColor = colorResource(R.color.text),
                colorResource(R.color.white),
                colorResource(R.color.disabled_button),
                colorResource(R.color.background)
            ),
            enabled = !username.isBlank() && !email.isBlank() && email.isEmail() && password == confirmPassword && password.length >= 6,
            onClick = onSignupButtonPressed,
        ) {
            Text(
                stringResource(R.string.register),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            modifier = Modifier
                .width(240.dp)
                .weight(1f), colors = ButtonColors(
                containerColor = colorResource(R.color.background),
                colorResource(R.color.white),
                colorResource(R.color.background),
                colorResource(R.color.background)
            ),
            onClick = onNavigateToLogin
        ) {
            Text(text = stringResource(R.string.already_have_account_text), color = Color.White)
        }
    }
}

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}