package bg.zahov.app.ui.custom

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CommonTextField(
    text: String,
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    onTextChange: (String) -> Unit
) {
    var isError by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(false) }

    TextField(
        value = text,
        onValueChange = {
            onTextChange(it)
            isError = false
            isActive = true
        },
        label = label,
        isError = isError,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
    )

    LaunchedEffect(text) {
        delay(2000)
        if (isActive) {
            isError = !validateText(text)
        }
    }
}

fun validateText(input: String): Boolean {
    return input.isNotEmpty() && input.length > 6
}
