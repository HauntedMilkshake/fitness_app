package bg.zahov.app.ui.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@Composable
fun WelcomeScreen(onSignup: () -> Unit, onLogin: () -> Unit) {
    FitnessTheme {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.padding(bottom = 24.dp),
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = stringResource(R.string.launcher_icon_description)
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 12.dp),
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier
                        .padding(top = 6.dp),
                    text = stringResource(R.string.motivational_quote),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column {
                WelcomeButton(
                    modifier = Modifier
                        .padding(bottom = 12.dp),
                    label = stringResource(R.string.register),
                    onClick = onSignup
                )
                WelcomeButton(
                    modifier = Modifier.testTag(stringResource(R.string.login_button_test_tag)),
                    label = stringResource(R.string.login),
                    onClick = onLogin
                )
            }
        }
    }
}

@Composable
fun WelcomeButton(modifier: Modifier = Modifier, label: String, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = modifier.width(240.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}