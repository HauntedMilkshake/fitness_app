package bg.zahov.app.ui.authentication.login

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import bg.zahov.app.ui.custom.CommonPasswordField
import bg.zahov.app.ui.custom.CommonTextField
import androidx.compose.runtime.LaunchedEffect

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isUserLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate(R.id.login_to_loading)
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(Unit) {
            Toast.makeText(context, userMessage, Toast.LENGTH_SHORT).show()
            viewModel.messageShown()
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChanged = { viewModel.onEmailChanged(it) },
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onPasswordVisibilityClicked = { viewModel.onPasswordVisibilityChanged() },
        onSendPasswordResetEmailClicked = { viewModel.sendPasswordResetEmail() },
        onLoginClicked = { viewModel.login() },
        onSignUpClicked = { navController.navigate(R.id.login_to_signup) }
    )


}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityClicked: () -> Unit,
    onSendPasswordResetEmailClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            "Login",
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
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            CommonTextField(
                text = uiState.email,
                leadingIcon = { Icon(painterResource(R.drawable.ic_profile), "Username") },
                label = { Text(stringResource(R.string.email_text_field_hint)) },
                onTextChange = onEmailChanged
            )

            CommonPasswordField(
                password = uiState.password,
                passwordVisible = uiState.isPasswordVisible,
                label = { Text(stringResource(R.string.password_text_field_hint)) },
                onPasswordChange = onPasswordChanged,
                onPasswordVisibilityChange = onPasswordVisibilityClicked
            )

            Text(
                text = stringResource(R.string.forgot_password),
                modifier = Modifier
                    .padding(top = 15.dp)
                    .clickable(onClick = onSendPasswordResetEmailClicked),
                style = TextStyle(color = colorResource(R.color.less_vibrant_text))
            )
            TextButton(
                onClick = onLoginClicked,
                modifier = Modifier
                    .padding(top = 15.dp)
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
                    .clickable(onClick = onSignUpClicked)
            )
        }
    }
}
