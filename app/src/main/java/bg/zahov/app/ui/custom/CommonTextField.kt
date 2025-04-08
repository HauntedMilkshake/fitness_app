package bg.zahov.app.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import bg.zahov.app.util.isEmail
import bg.zahov.fitness.app.R
import kotlinx.coroutines.delay

@Composable
fun CommonTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    ), shape: Shape = RoundedCornerShape(10.dp),
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    isEmail: Boolean = false,
    enabled: Boolean = true,
    testTagString: String = ""
) {
    var isError by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier.semantics { testTag = testTagString },
        value = text,
        onValueChange = {
            onTextChange(it)
            isError = false
            isActive = true
        },
        isError = isError,
        enabled = enabled,
        label = label,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
        supportingText = {
            if (isError) {
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
        }
    )

    LaunchedEffect(text) {
        delay(1000)
        if (isActive) {
            isError = (if (isEmail) !text.isEmail() else text.isBlank())
        }
    }
}
