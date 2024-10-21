package bg.zahov.app.ui.custom

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bg.zahov.fitness.app.R

@Composable
fun CommonTextField(
    text: MutableState<String>,
    icon: Int = R.drawable.ic_email,
    placeholder: String
){
    TextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(text = placeholder) },
        modifier = Modifier
            .padding(top = 20.dp),
        shape = RoundedCornerShape(10.dp),
        leadingIcon = { Icon(
            painter = painterResource(id = icon),
            contentDescription = null
        )
        },
        singleLine = true)
}