package bg.zahov.app.ui.login

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bg.zahov.fitness.app.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bg.zahov.app.ui.custom.CommonPasswordField
import bg.zahov.app.ui.custom.CommonTextField


@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel() , nav: NavController) {
    val password = remember { mutableStateOf("")}
    val mail = remember { mutableStateOf("")}
    val interactionSource = remember { MutableInteractionSource() }
    Toasts( loginViewModel = loginViewModel,
        nav = {nav.navigate(R.id.login_to_loading)})
    Column(modifier = Modifier
        .fillMaxSize()) {
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
                .width(250.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            CommonTextField(mail, R.drawable.ic_email, "Mail")
            CommonPasswordField(password, "Password",)

            Text(
                text = stringResource(R.string.forgot_password),
                modifier = Modifier
                    .padding(top = 15.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null) { loginViewModel.sendPasswordResetEmail(mail.value) },
                style = TextStyle(color = colorResource(R.color.less_vibrant_text))
            )
            TextButton(
                onClick = {
                    loginViewModel.login(mail.value, password.value)
                },
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
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null) {
                        nav.navigate(R.id.login_to_signup)
                    }
            )
        }
    }
}

@Composable
fun Toasts(loginViewModel: LoginViewModel = viewModel(), nav: ()->Unit){
    val context = LocalContext.current
    val uiState by loginViewModel.uiState.collectAsState()
    when(uiState){
        is LoginViewModel.UiState.Authenticated->{
            nav()
        }
        is LoginViewModel.UiState.Error->{
            Toast.makeText(context,
                (uiState as LoginViewModel.UiState.Error).message,
                Toast.LENGTH_SHORT).show()
        }
        is LoginViewModel.UiState.Notification->{
            Toast.makeText(context,
                (uiState as LoginViewModel.UiState.Notification).message,
                Toast.LENGTH_SHORT).show()
        }
        LoginViewModel.UiState.Default -> {/*TODO(Nothing to do)*/}
    }
}