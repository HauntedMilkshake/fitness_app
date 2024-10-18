package bg.zahov.app.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bg.zahov.fitness.app.R


@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun LoginScreen() {
    Column(modifier = Modifier
        .fillMaxSize()) {
        Text(
            "Login",
            fontSize = 40.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 130.dp)
        )
        Column(
            modifier = Modifier
                .width(250.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            val password = mutableStateOf("")
            CommonTextField(password, R.drawable.ic_email, "Mail")
            val mail = mutableStateOf("")
            CommonPasswordField(mail, "placeholder",)

            Text(
               text = stringResource(R.string.forgot_password),
                modifier = Modifier
                    .padding(top = 15.dp),
                style = TextStyle( color = colorResource(R.color.less_vibrant_text))
            )
        }
    }
}

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
        )},
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        visualTransformation = if (visibility.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}

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
        )},
        singleLine = true)
}
