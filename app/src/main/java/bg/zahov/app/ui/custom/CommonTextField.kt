package bg.zahov.app.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bg.zahov.app.util.isEmail
import bg.zahov.fitness.app.R

@Composable
fun CommonTextField(
    text: String,
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    ),
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEmail: Boolean = false
) {
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = { text ->
            onTextChange(text)
        },
        isError = if (isEmail) !text.isEmail() else text.isBlank(),
        label = label,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
        supportingText = {
            if (isEmail) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.wrong_email)
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.no_empty_fields)
                )
            }
        }
    )
}
