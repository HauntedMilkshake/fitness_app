package bg.zahov.app.ui.custom

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import bg.zahov.fitness.app.R

@Composable
fun CommonPasswordField(
    password: String,
    passwordVisible: Boolean,
    label: @Composable (() -> Unit) = { Text("Password") },
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
        unfocusedIndicatorColor = Color.Transparent
    ),
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    TextField(
        value = password,
        onValueChange = {
            onPasswordChange(it)
        },
        label = label,
        modifier = modifier,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onPasswordVisibilityChange() }) {
                Icon(
                    painterResource(if (passwordVisible) R.drawable.ic_password_hidden else R.drawable.ic_password_visible),
                    ""
                )
            }
        }
    )
}