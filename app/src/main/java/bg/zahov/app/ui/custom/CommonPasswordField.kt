package bg.zahov.app.ui.custom

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bg.zahov.fitness.app.R
import kotlinx.coroutines.delay

@Composable
fun CommonPasswordField(
    modifier: Modifier = Modifier,
    password: String,
    passwordVisible: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit) = {
        Icon(
            painterResource(R.drawable.ic_password),
            "Password"
        )
    },
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    ),
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    var isError by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = password,
        onValueChange = {
            onPasswordChange(it)
            isError = false
            isActive = true
        },

        label = label,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
        isError = isError,
        enabled = enabled,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                onPasswordVisibilityChange(passwordVisible)
            }) {
                Icon(
                    painterResource(if (passwordVisible) R.drawable.ic_password_hidden else R.drawable.ic_password_visible),
                    ""
                )
            }
        },
        supportingText = {
            if (isError) {
                Text(
                    text = stringResource(R.string.password_helper_text),
                    fontSize = 12.sp
                )
            }
        }
    )

    LaunchedEffect(password) {
        delay(1000)
        if (isActive) {
            isError = (password.isBlank() || password.length < 6)
        }
    }
}
