package bg.zahov.app.ui.custom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import bg.zahov.fitness.app.R

@Composable
fun CommonPasswordField(
    text: MutableState<String>,
    placeholder: String,
    trailingIcon: Int = R.drawable.ic_password,
    visibility: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    TextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(text = placeholder) },
        trailingIcon = {
            Icon(
                painter = painterResource(id = trailingIcon),
                contentDescription = "",
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        visibility.value = !visibility.value
                    },
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = { Icon(
            painter = painterResource(id = R.drawable.ic_password),
            contentDescription = null
        )
        },
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        visualTransformation = if (visibility.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}