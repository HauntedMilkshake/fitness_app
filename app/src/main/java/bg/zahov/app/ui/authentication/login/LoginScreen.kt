package bg.zahov.app.ui.authentication.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.fitness.app.R
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.ui.custom.CommonPasswordField
import bg.zahov.app.ui.custom.CommonTextField

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel(), nav: (Int) -> Unit) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    if (uiState.isLoggedInfo) {
        LaunchedEffect(Unit) {
            nav(R.id.login_to_loading)
        }
    }

    uiState.message?.let { message ->
        LaunchedEffect(Unit) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            loginViewModel.shownMessage()
        }
    }
    LoginContent(
        email = uiState.email,
        onEmailChange = { loginViewModel.onEmailChange(email = it) },
        password = uiState.password,
        onPasswordChange = { loginViewModel.onPasswordChange(password = it) },
        passwordVisibility = uiState.passwordVisibility,
        onPasswordVisibilityChange = { loginViewModel.onPasswordVisibilityChange() },
        navigateSignUp = { nav(R.id.login_to_signup) },
        logIn = { loginViewModel.login() },
        resetPassword = { loginViewModel.sendPasswordResetEmail() }
        )
}

@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisibility: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    navigateSignUp: () -> Unit,
    logIn: () -> Unit,
    resetPassword: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.login),
            fontSize = 40.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 130.dp)
        )
        Column(
            modifier = Modifier
                .width(240.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommonTextField(
                text = email,
                leadingIcon = { Icon(painterResource(R.drawable.ic_profile), "Username") },
                label = { Text(stringResource(R.string.email_text_field_hint)) },
                onTextChange = { onEmailChange(it) })

            CommonPasswordField(
                password = password,
                passwordVisible = passwordVisibility,
                label = { Text(stringResource(R.string.password_text_field_hint)) },
                onPasswordChange = { onPasswordChange(it) },
                onPasswordVisibilityChange = { onPasswordVisibilityChange() })

            Text(
                text = stringResource(R.string.forgot_password),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { resetPassword() },
                style = TextStyle(color = colorResource(R.color.less_vibrant_text))
            )
            TextButton(
                onClick = { logIn() },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonColors(
                    containerColor = colorResource(R.color.text),
                    contentColor = Color.White,
                    disabledContainerColor = colorResource(R.color.text),
                    disabledContentColor = colorResource(R.color.text)
                )
            ) {
                Text(text = "Login")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterHorizontally),
        ) {

            Text(
                text = stringResource(R.string.no_account_text),
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { navigateSignUp() }
            )
        }
    }
}