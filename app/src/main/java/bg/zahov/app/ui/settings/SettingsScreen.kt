package bg.zahov.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R
import java.time.format.TextStyle

@Composable
@Preview
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Column {
            SettingsText(stringResource(R.string.profile))
            SettingsButton(text = "pog") { }
        }
        Column {
            SettingsText(stringResource(R.string.units_and_locale_text))
            SettingsRadioButton(
                title = stringResource(R.string.language_text),
                text = ""
            ) { }
            SettingsRadioButton(
                title = stringResource(R.string.units_text),
                text = ""
            ) { }
        }
        Column {
            SettingsText(stringResource(R.string.general_text))
            SettingsSwitchButton(
                title = stringResource(R.string.sound_effects_text),
                text = stringResource(R.string.no_rest_timer_included_text),
                checked = false
            ) { }
            SettingsRadioButton(
                title = stringResource(R.string.theme_text),
                text = ""
            ) { }
        }
        Column {
            SettingsText(stringResource(R.string.rest_timer_text))
            SettingsRadioButton(
                title = stringResource(R.string.timer_increment_value_text),
                text = ""
            ) { }
            SettingsSwitchButton(
                title = stringResource(R.string.vibrate_upon_finish_text),
                text = "",
                checked = false
            ) { }
            SettingsRadioButton(
                title = stringResource(R.string.sound_text),
                text = ""
            ) { }
        }
        Column {
            SettingsText(stringResource(R.string.advanced_settings_text))
            SettingsSwitchButton(
                title = stringResource(R.string.show_update_template),
                text = stringResource(R.string.username_text),
                checked = false
            ) { }
            SettingsSwitchButton(
                title = stringResource(R.string.show_update_template),
                text = stringResource(R.string.username_text),
                checked = false
            ) { }
            SettingsSwitchButton(
                title = stringResource(R.string.show_update_template),
                text = stringResource(R.string.username_text),
                checked = false
            ) { }
            Column {
                SettingsButton(stringResource(R.string.github_text)) { }
                SettingsButton(stringResource(R.string.bug_report_text)) { }
            }
            Column {
                Button(
                    onClick = {}
                ) {
                    Text(text = stringResource(R.string.sign_out_button_text))
                }
                Button(
                    onClick = {}
                ) {
                    Text(text = stringResource(R.string.delete_account_text))
                }
            }
        }
    }
}

@Composable
fun SettingsButton(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 10.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.white)

        )
    }
}

@Composable
fun SettingsRadioButton(title: String, text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, bottom = 10.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.white)
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, bottom = 10.dp),
            fontSize = 12.sp,
            color = colorResource(R.color.text)
        )
    }
}

@Composable
fun SettingsSwitchButton(title: String, text: String, checked: Boolean, onChecked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = title,
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, bottom = 10.dp),
                fontSize = 16.sp,
                color = colorResource(R.color.white)
            )
            Text(
                text = text,
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, bottom = 10.dp),
                fontSize = 12.sp,
                color = colorResource(R.color.text)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onChecked() },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SettingsText(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        color = colorResource(R.color.white),
        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
    )
}