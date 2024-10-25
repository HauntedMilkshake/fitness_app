package bg.zahov.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R

@Composable
@Preview
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        ColumnTemplate {
            SettingsText(stringResource(R.string.profile))
            SettingsButton(text = "pog") { }
        }
        ColumnTemplate {
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
        ColumnTemplate {
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
        ColumnTemplate {
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
        ColumnTemplate {
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
        }
        ColumnTemplate {
            SettingsButton(stringResource(R.string.github_text)) { }
            SettingsButton(stringResource(R.string.bug_report_text)) { }
        }
        ColumnTemplate {
            Button(
                modifier = Modifier.width(240.dp),
                onClick = {}
            ) {
                Text(text = stringResource(R.string.sign_out_button_text))
            }
            Button(
                modifier = Modifier.width(240.dp),
                onClick = {}
            ) {
                Text(text = stringResource(R.string.delete_account_text))
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
            .padding(top = 16.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(top = 20.dp, start = 20.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.white)
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 20.dp, start = 20.dp),
            fontSize = 12.sp,
            color = colorResource(R.color.text)
        )
    }
}

@Composable
fun SettingsSwitchButton(title: String, text: String, checked: Boolean, onChecked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                color = colorResource(R.color.white)
            )
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
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
        modifier = Modifier.padding(top = 20.dp, start = 16.dp)
    )
}

@Composable
fun ColumnTemplate(items: @Composable (() -> Unit)) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items()
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(top = 16.dp , bottom = 8.dp),
        color = Color.White
    )
}