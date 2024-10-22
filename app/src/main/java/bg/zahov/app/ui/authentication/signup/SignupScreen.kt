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
import androidx.navigation.NavController
import bg.zahov.app.ui.authentication.AuthenticationState
import bg.zahov.app.ui.authentication.UiInfo
import bg.zahov.app.ui.custom.CommonPasswordField
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.fitness.app.R


@Composable
fun SignupScreen(signupViewModel: SignupViewModel = viewModel(), navController: NavController) {
    val state = signupViewModel.state.collectAsStateWithLifecycle()
    var uiInfo = (state.value as? AuthenticationState.Default)?.uiInfo ?: UiInfo()
    SignupContent(
        onNameChange = { signupViewModel.onUsernameChange(it) },
        onEmailChange = { signupViewModel.onEmailChange(it) },
        onPasswordChange = { signupViewModel.onPasswordChange(it) },
        onConfirmPasswordChange = { signupViewModel.onConfirmPasswordChange(it) },
        onSignupButtonPressed = { username, email, password, confirmPassword ->
            signupViewModel.signUp(
                username,
                email,
                password,
                confirmPassword
            )
        },
        onPasswordVisibilityChange = { signupViewModel.onPasswordVisibilityChange(it) },
        username = uiInfo.username,
        email = uiInfo.mail,
        password = uiInfo.password,
        confirmPassword = uiInfo.confirmPassword,
        showPassword = uiInfo.passwordVisibility,
        navController = navController
    )

    when (state.value) {
        AuthenticationState.Authenticate -> navController.navigate(R.id.signup_to_loading)
        is AuthenticationState.Default -> uiInfo =
            (state.value as AuthenticationState.Default).uiInfo

        is AuthenticationState.Notify -> {
            val capture = state.value as AuthenticationState.Notify
            showToast(capture.message, LocalContext.current)
            uiInfo = capture.uiInfo
        }
    }
}

@Composable
fun SignupContent(
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignupButtonPressed: (String, String, String, String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    username: String,
    email: String,
    password: String,
    confirmPassword: String,
    showPassword: Boolean,
    navController: NavController
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 150.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 20.dp),
            text = "Sign up",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
            CommonTextField(
                text = username,
                label = { Text(stringResource(R.string.username_text)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_profile), "Username") },
                onTextChange = {

                    onNameChange(it)
                })
            CommonTextField(
                text = email,
                label = { Text(stringResource(R.string.email_text_field_hint)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_email), "Email") },
                onTextChange = {
                    onEmailChange(it)
                })
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
                colorResource(R.color.background),
                colorResource(R.color.background)
            ),
            onClick = {
                onSignupButtonPressed(username, email, password, confirmPassword)
            }) {
            Text(
                stringResource(R.string.register),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Button(modifier = Modifier
            .width(240.dp)
            .weight(1f), colors = ButtonColors(
            containerColor = colorResource(R.color.background),
            colorResource(R.color.white),
            colorResource(R.color.background),
            colorResource(R.color.background)
        ),
            onClick = {
                navController.navigate(R.id.signup_to_login)
            }) {
            Text(text = stringResource(R.string.already_have_account_text), color = Color.White)
        }
    }
}

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
