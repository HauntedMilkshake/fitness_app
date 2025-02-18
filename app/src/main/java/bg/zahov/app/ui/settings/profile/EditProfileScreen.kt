package bg.zahov.app.ui.settings.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.ui.custom.CommonPasswordField
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.fitness.app.R

@Composable
fun EditProfileScreen(viewModel: EditProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val toastMessage by ToastManager.messages.collectAsStateWithLifecycle()
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, context.getString(message.messageResId), Toast.LENGTH_SHORT)
                .show()
        }
    }

    EditProfileContent(
        authenticated = uiState.authenticated,
        username = uiState.username,
        password = uiState.password,
        passwordVisibility = uiState.passwordVisibility,
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onPasswordVisibilityChange = { viewModel.onPasswordVisibilityChange() },
        updatePassword = { viewModel.updatePassword() },
        updateUsername = { viewModel.updateUsername() },
        resetPassword = { viewModel.sendPasswordResetLink() },
        dialog = { onDismiss ->
            AuthenticateDialog(password = uiState.passwordDialog,
                passwordVisibility = uiState.passwordVisibilityDialog,
                onPasswordChange = { viewModel.onPasswordChangeDialog(it) },
                onPasswordVisibilityChange = { viewModel.onPasswordVisibilityChangeDialog() },
                onDismiss = onDismiss,
                authenticate = { viewModel.unlockFields(it) })
        }
    )
}

@Composable
fun EditProfileContent(
    authenticated: Boolean,
    username: String,
    password: String,
    passwordVisibility: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    updatePassword: () -> Unit,
    updateUsername: () -> Unit,
    resetPassword: () -> Unit,
    dialog: @Composable (onDismiss: () -> Unit) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog && !authenticated) {
        dialog { showDialog = false }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTextField(
            text = username,
            leadingIcon = { Icon(painterResource(R.drawable.ic_profile), "Username") },
            label = { Text(stringResource(R.string.username_text)) },
            onTextChange = { onUsernameChange(it) },
            enabled = authenticated
        )
        Button(
            colors = ButtonColors(
                contentColor = colorResource(R.color.white),
                containerColor = colorResource(R.color.purple_200),
                disabledContentColor = colorResource(R.color.less_vibrant_text),
                disabledContainerColor = colorResource(R.color.disabled_button)
            ),
            onClick = updateUsername,
            enabled = authenticated && username.isNotEmpty()
        ) {
            Text(text = stringResource(R.string.update_username))
        }
        CommonPasswordField(
            password = password,
            passwordVisible = passwordVisibility,
            label = { Text(stringResource(R.string.password_text_field_hint)) },
            onPasswordChange = { onPasswordChange(it) },
            onPasswordVisibilityChange = { onPasswordVisibilityChange() },
            enabled = authenticated
        )
        Button(
            colors = ButtonColors(
                contentColor = colorResource(R.color.white),
                containerColor = colorResource(R.color.purple_200),
                disabledContentColor = colorResource(R.color.less_vibrant_text),
                disabledContainerColor = colorResource(R.color.disabled_button)
            ),
            onClick = updatePassword,
            enabled = authenticated && password.length >= 6
        ) {
            Text(text = stringResource(R.string.update_password))
        }
        Button(
            colors = ButtonColors(
                contentColor = colorResource(R.color.white),
                containerColor = colorResource(R.color.purple_200),
                disabledContentColor = colorResource(R.color.less_vibrant_text),
                disabledContainerColor = colorResource(R.color.disabled_button)
            ),
            onClick = { showDialog = true },
            enabled = !authenticated
        ) {
            Text(text = stringResource(R.string.confirm_authentication))
        }
        Button(
            colors = ButtonColors(
                contentColor = colorResource(R.color.white),
                containerColor = colorResource(R.color.purple_200),
                disabledContentColor = colorResource(R.color.less_vibrant_text),
                disabledContainerColor = colorResource(R.color.disabled_button)
            ),
            onClick = resetPassword,
        ) {
            Text(text = stringResource(R.string.reset_password_text))
        }
    }
}