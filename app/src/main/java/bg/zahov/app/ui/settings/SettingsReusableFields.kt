package bg.zahov.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import bg.zahov.fitness.app.R

@Composable
fun SettingsButton(text: String, onClick: @Composable () -> Unit) {
    val showError = remember { mutableStateOf(false) }
    if (showError.value) {
        onClick()
        showError.value = false
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showError.value = true }
            .padding(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = colorResource(R.color.white)

        )
    }
}

@Composable
fun SettingsRadioButton(
    title: String,
    text: String,
    dialog: @Composable (onDismiss: () -> Unit) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        dialog { showDialog = false }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = colorResource(R.color.white)
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 4.dp, start = 12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = colorResource(R.color.text)
        )
    }
}

@Composable
fun SettingsSwitchButton(
    title: String,
    text: String = "",
    checked: Boolean,
    onChecked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(R.color.white)
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                color = colorResource(R.color.text)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onChecked() },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 12.dp),
        )
    }
}

@Composable
fun SettingsText(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.titleLarge,
    color = Color.White,
    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
)