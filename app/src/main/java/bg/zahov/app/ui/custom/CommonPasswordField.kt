package bg.zahov.app.ui.custom

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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

@Composable
fun CommonPasswordField(
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
    modifier: Modifier = Modifier

) {
    TextField(
        modifier = modifier,
        value = password,
        onValueChange = {
            onPasswordChange(it)
        },

        label = label,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
        isError = (password.isBlank() || password.length < 6),
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
            Text(
                text = stringResource(R.string.password_helper_text),
                fontSize = 12.sp
            )
        }
    )
}
