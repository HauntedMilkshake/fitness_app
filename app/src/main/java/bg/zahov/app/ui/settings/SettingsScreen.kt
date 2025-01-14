package bg.zahov.app.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.state.TypeSettings
import bg.zahov.app.ui.custom.CommonDivider
import bg.zahov.fitness.app.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    navigateBack: () -> Unit,
    navigateEditProfile: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.navigateBack) {
        LaunchedEffect(Unit) {
            navigateBack()
        }
    }
    SettingsContent(
        resetTimer = 0,
        changeResetTimer = { onDismiss ->
            RadioSettingsDialog(
                type = TypeSettings.REST_TIMER_SETTING,
                onDismissRequest = onDismiss,
                selected = 0,
                setSelected = { type: TypeSettings, value: Any ->
                    viewModel.writeNewSetting(type = type, newValue = value)
                })
        },
        sound = "",
        changeSound = { onDismiss ->
            RadioSettingsDialog(
                type = TypeSettings.SOUND_SETTING,
                onDismissRequest = onDismiss,
                selected = "",
                setSelected = { type: TypeSettings, value: Any ->
                    viewModel.writeNewSetting(type = type, newValue = value)
                })
        },
        theme = "",
        changeTheme = { onDismiss ->
            RadioSettingsDialog(
                type = TypeSettings.THEME_SETTING,
                onDismissRequest = onDismiss,
                selected = "",
                setSelected = { type: TypeSettings, value: Any ->
                    viewModel.writeNewSetting(type = type, newValue = value)
                })

        },
        units = "",
        changeUnits = { onDismiss ->
            RadioSettingsDialog(
                type = TypeSettings.UNIT_SETTING,
                onDismissRequest = onDismiss,
                selected = "",
                setSelected = { type: TypeSettings, value: Any ->
                    viewModel.writeNewSetting(type = type, newValue = value)
                })

        },
        language = "",
        changeLanguage = { onDismiss ->
            RadioSettingsDialog(
                type = TypeSettings.LANGUAGE_SETTING,
                onDismissRequest = onDismiss,
                selected = "",
                setSelected = { type: TypeSettings, value: Any ->
                    viewModel.writeNewSetting(type = type, newValue = value)
                })
        },
        navigateEditProfile = { navigateEditProfile() },
        enableSound = false,
        enableSoundChange = {
            viewModel.writeNewSetting(
                type = TypeSettings.SOUND_EFFECTS_SETTING,
                newValue = false
            )
        },
        enableSync = false,
        enableSyncChange = {
            viewModel.writeNewSetting(
                type = TypeSettings.AUTOMATIC_SYNC_SETTING,
                newValue = false
            )
        },
        enableVibrate = false,
        enableVibrateChange = {
            viewModel.writeNewSetting(
                type = TypeSettings.VIBRATION_SETTING,
                newValue = false
            )
        },
        showUpdate = false,
        showUpdateChange = {
            viewModel.writeNewSetting(
                type = TypeSettings.UPDATE_TEMPLATE_SETTING,
                newValue = false
            )
        },
        enableWatch = false,
        useWatchChange = {
            viewModel.writeNewSetting(
                type = TypeSettings.WATCH_SETTINGS,
                newValue = false
            )
        },
        github = {
            OpenLink(link = "https://github.com/HauntedMilkshake/fitness_app")
        },
        bugReport = {
            OpenLink(link = "https://github.com/HauntedMilkshake/fitness_app/issues")
        },
        deleteAccount = {
            viewModel.deleteAccount()
        },
        logout = {
            viewModel.logout()
        }
    )
}

@Composable
fun SettingsContent(
    sound: String,
    resetTimer: Int,
    theme: String,
    units: String,
    language: String,
    enableSound: Boolean,
    enableVibrate: Boolean,
    enableSync: Boolean,
    showUpdate: Boolean,
    enableWatch: Boolean,
    changeSound: @Composable (onDismiss: () -> Unit) -> Unit,
    changeResetTimer: @Composable (onDismiss: () -> Unit) -> Unit,
    changeTheme: @Composable (onDismiss: () -> Unit) -> Unit,
    changeUnits: @Composable (onDismiss: () -> Unit) -> Unit,
    changeLanguage: @Composable (onDismiss: () -> Unit) -> Unit,
    navigateEditProfile: () -> Unit,
    enableSoundChange: () -> Unit,
    enableVibrateChange: () -> Unit,
    enableSyncChange: () -> Unit,
    showUpdateChange: () -> Unit,
    useWatchChange: () -> Unit,
    github: @Composable () -> Unit,
    bugReport: @Composable () -> Unit,
    deleteAccount: () -> Unit,
    logout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsText(stringResource(R.string.profile))
        SettingsButton(text = stringResource(R.string.edit_profile_text)) { navigateEditProfile() }
        CommonDivider()
        SettingsText(stringResource(R.string.units_and_locale_text))
        SettingsRadioButton(
            title = stringResource(R.string.language_text),
            text = language,
            dialog = { onDismiss -> changeLanguage(onDismiss) })
        SettingsRadioButton(
            title = stringResource(R.string.units_text),
            text = units,
            dialog = { onDismiss -> changeUnits(onDismiss) })
        CommonDivider()
        SettingsText(stringResource(R.string.general_text))
        SettingsSwitchButton(
            title = stringResource(R.string.sound_effects_text),
            text = stringResource(R.string.no_rest_timer_included_text),
            checked = enableSound
        ) { enableSoundChange() }
        SettingsRadioButton(
            title = stringResource(R.string.theme_text),
            text = theme,
            dialog = { onDismiss -> changeTheme(onDismiss) })
        CommonDivider()
        SettingsText(stringResource(R.string.rest_timer_text))
        SettingsRadioButton(
            title = stringResource(R.string.timer_increment_value_text),
            text = resetTimer.toString(),
            dialog = { onDismiss -> changeResetTimer(onDismiss) })
        SettingsSwitchButton(
            title = stringResource(R.string.vibrate_upon_finish_text),
            checked = enableVibrate
        ) { enableVibrateChange() }
        SettingsRadioButton(
            title = stringResource(R.string.sound_text),
            text = sound,
            dialog = { onDismiss -> changeSound(onDismiss) })
        CommonDivider()
        SettingsText(stringResource(R.string.advanced_settings_text))
        SettingsSwitchButton(
            title = stringResource(R.string.sync_option_text),
            text = stringResource(R.string.sync_option_explain),
            checked = enableSync
        ) { enableSyncChange() }
        SettingsSwitchButton(
            title = stringResource(R.string.show_update_template),
            text = stringResource(R.string.show_update_template_explain),
            checked = showUpdate
        ) { showUpdateChange() }
        SettingsSwitchButton(
            title = stringResource(R.string.use_watch_option),
            checked = enableWatch
        ) { useWatchChange() }
        CommonDivider()
        SettingsButton(stringResource(R.string.github_text)) { github() }
        SettingsButton(stringResource(R.string.bug_report_text)) { bugReport() }
        CommonDivider()
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .width(240.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { logout() },
                colors = ButtonColors(
                    Color.Red, Color.White, Color.Red, Color.White
                )
            ) {
                Text(text = stringResource(R.string.sign_out_button_text))
            }
            Button(
                modifier = Modifier
                    .width(240.dp)
                    .padding(bottom = 80.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { deleteAccount() },
                colors = ButtonColors(
                    Color.Red, Color.White, Color.Red, Color.White
                )
            ) {
                Text(text = stringResource(R.string.delete_account_text))
            }
        }
    }
}

@Composable
private fun OpenLink(link: String) {
    val context = LocalContext.current
    val noAppMessage = stringResource(R.string.no_application)
    val linkErrorMessage = stringResource(R.string.link_error)

    LaunchedEffect(Unit) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                noAppMessage,
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                linkErrorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}