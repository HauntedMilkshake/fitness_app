package bg.zahov.app.ui.settings.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import bg.zahov.app.ui.custom.CommonPasswordField
import bg.zahov.fitness.app.R

@Composable
fun AuthenticateDialog(
    authenticate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardColors(
                colorResource(R.color.background),
                colorResource(R.color.white),
                colorResource(R.color.background),
                colorResource(R.color.white)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CommonPasswordField(
                    password = password,
                    passwordVisible = passwordVisibility,
                    onPasswordChange = { password = it },
                    onPasswordVisibilityChange = { passwordVisibility = !passwordVisibility })
                Button(onClick = { authenticate(password) }) {
                    Text(text = stringResource(R.string.confirm_password_text))
                }
            }
        }
    }
}