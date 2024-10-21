package bg.zahov.app.ui.custom

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import bg.zahov.fitness.app.R

@Composable
fun CommonPasswordField(
    password: MutableState<String>,
    passwordVisible: MutableState<Boolean> = mutableStateOf(false),
    label: @Composable (() -> Unit) = { Icon(painterResource(R.drawable.ic_password), "Password") },
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    onPasswordChange: (String) -> Unit
) {
    TextField(
        value = password.value,
        onValueChange = {
            password.value = it
            onPasswordChange(it)
        },
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