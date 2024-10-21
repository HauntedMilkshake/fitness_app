package bg.zahov.app.ui.signup

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bg.zahov.fitness.app.R


@Composable
fun SignupScreen(signupViewModel: SignupViewModel = viewModel(), navController: NavController) {
    var username = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var confirmPassword = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var showPassword = remember { mutableStateOf(false) }
    val state = signupViewModel.state.collectAsState()

    when (state.value) {
        SignupViewModel.State.Default -> { /* no-op */
        }

        is SignupViewModel.State.Authentication -> {
            ShowToast(
                (state.value as SignupViewModel.State.Authentication).aMessage,
                LocalContext.current
            )
            navController.navigate(R.id.signup_to_loading)
        }

        is SignupViewModel.State.Notify -> {
            ShowToast((state.value as SignupViewModel.State.Notify).nMessage, LocalContext.current)
        }
    }

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
            GenerateTextField(
                textFieldValue = username,
                label = { Text(stringResource(R.string.username_text)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_profile), "Username") },
            )
            GenerateTextField(
                textFieldValue = email,
                label = { Text(stringResource(R.string.email_text_field_hint)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_email), "Email") },
            )
            GeneratePasswordTextField(
                password = password,
                passwordVisible = showPassword,
                label = { Text(stringResource(R.string.password_text_field_hint)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_password), "Password") },
            )
            GeneratePasswordTextField(
                password = confirmPassword,
                passwordVisible = showPassword,
                label = { Text(stringResource(R.string.confirm_password_text)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_password), "Confirm Password") },
            )
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
                signupViewModel.signUp(
                    username.value,
                    email.value,
                    password.value,
                    confirmPassword.value
                )
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

@Composable
fun GeneratePasswordTextField(
    password: MutableState<String>,
    passwordVisible: MutableState<Boolean> = mutableStateOf(false),
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )
) {
    TextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = label,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                passwordVisible.value = !passwordVisible.value
            }) {
                Icon(
                    painterResource(if (passwordVisible.value) R.drawable.ic_password_hidden else R.drawable.ic_password_visible),
                    ""
                )
            }
        }
    )
}

@Composable
fun GenerateTextField(
    textFieldValue: MutableState<String>,
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )
) {

    TextField(
        value = textFieldValue.value,
        onValueChange = { textFieldValue.value = it },
        label = label,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors
    )
}

fun ShowToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
