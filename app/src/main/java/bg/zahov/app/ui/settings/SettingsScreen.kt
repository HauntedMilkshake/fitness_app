package bg.zahov.app.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.local.RealmManager.Companion.AUTOMATIC_SYNC_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.LANGUAGE_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.REST_TIMER_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.SOUND_EFFECTS_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.SOUND_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.THEME_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.UNIT_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.UPDATE_TEMPLATE_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.VIBRATION_SETTING
import bg.zahov.app.data.local.RealmManager.Companion.WATCH_SETTINGS
import bg.zahov.app.data.model.LanguageKeys.Companion.languages
import bg.zahov.app.data.model.SoundKeys.Companion.sounds
import bg.zahov.app.data.model.ThemeKeys.Companion.theme
import bg.zahov.app.data.model.UnitsKeys.Companion.units
import bg.zahov.fitness.app.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    navigateBack: () -> Unit,
    navigateEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.returnBack) {
        navigateBack()
    }
    SettingsContent(
        resetTimer = uiState.data.restTimer,
        changeResetTimer = { onDismiss ->
            RadioSettingsDialog(
                title = REST_TIMER_SETTING,
                items = listOf(30, 15, 5),
                onDismissRequest = onDismiss,
                selected = uiState.data.restTimer.toString(),
                setSelected = { viewModel.writeNewSetting(title = REST_TIMER_SETTING, it) })
        },
        sound = uiState.data.soundSettings,
        changeSound = { onDismiss ->
            RadioSettingsDialog(
                title = SOUND_SETTING,
                items = sounds,
                onDismissRequest = onDismiss,
                selected = uiState.data.soundSettings,
                setSelected = { viewModel.writeNewSetting(title = SOUND_SETTING, it) })
        },
        theme = uiState.data.theme,
        changeTheme = { onDismiss ->
            RadioSettingsDialog(
                title = THEME_SETTING,
                items = theme,
                onDismissRequest = onDismiss,
                selected = uiState.data.theme,
                setSelected = { viewModel.writeNewSetting(title = THEME_SETTING, it) })

        },
        units = uiState.data.units,
        changeUnits = { onDismiss ->
            RadioSettingsDialog(
                title = UNIT_SETTING,
                items = units,
                onDismissRequest = onDismiss,
                selected = uiState.data.units,
                setSelected = { viewModel.writeNewSetting(title = UNIT_SETTING, it) })

        },
        language = uiState.data.language,
        changeLanguage = { onDismiss ->
            RadioSettingsDialog(
                title = LANGUAGE_SETTING,
                items = languages,
                onDismissRequest = onDismiss,
                selected = uiState.data.language,
                setSelected = { viewModel.writeNewSetting(title = LANGUAGE_SETTING, it) })

        },
        navigateEditProfile = { navigateEditProfile() },
        enableSound = uiState.data.soundEffects,
        enableSoundChange = {
            viewModel.writeNewSetting(
                title = SOUND_EFFECTS_SETTING,
                newValue = !uiState.data.soundEffects
            )
        },
        enableSync = uiState.data.automaticSync,
        enableSyncChange = {
            viewModel.writeNewSetting(
                title = AUTOMATIC_SYNC_SETTING,
                newValue = !uiState.data.automaticSync
            )
        },
        enableVibrate = uiState.data.vibration,
        enableVibrateChange = {
            viewModel.writeNewSetting(
                title = VIBRATION_SETTING,
                newValue = !uiState.data.vibration
            )
        },
        showUpdate = uiState.data.updateTemplate,
        showUpdateChange = {
            viewModel.writeNewSetting(
                title = UPDATE_TEMPLATE_SETTING,
                newValue = !uiState.data.updateTemplate
            )
        },
        enableWatch = uiState.data.enableWatch,
        useWatchChange = {
            viewModel.writeNewSetting(
                title = WATCH_SETTINGS,
                newValue = !uiState.data.enableWatch
            )
        },
        github = {
            openLink(
                context = context,
                link = "https://github.com/HauntedMilkshake/fitness_app"
            )
        },
        bugReport = {
            openLink(
                context = context,
                link = "https://github.com/HauntedMilkshake/fitness_app/issues"
            )
        },
        deleteAccount = { viewModel.deleteAccount() },
        logout = { viewModel.logout() })
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
    github: () -> Unit,
    bugReport: () -> Unit,
    deleteAccount: () -> Unit,
    logout: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        ColumnTemplate {
            SettingsText(stringResource(R.string.profile))
            SettingsButton(text = stringResource(R.string.edit_profile_text)) { navigateEditProfile() }
        }
        ColumnTemplate {
            SettingsText(stringResource(R.string.units_and_locale_text))
            SettingsRadioButton(
                title = stringResource(R.string.language_text),
                text = language,
                dialog = { onDismiss -> changeLanguage(onDismiss) })
            SettingsRadioButton(
                title = stringResource(R.string.units_text),
                text = units,
                dialog = { onDismiss -> changeUnits(onDismiss) })
        }
        ColumnTemplate {
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
        }
        ColumnTemplate {
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
        }
        ColumnTemplate {
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
        }
        ColumnTemplate {
            SettingsButton(stringResource(R.string.github_text)) { github() }
            SettingsButton(stringResource(R.string.bug_report_text)) { bugReport() }
        }
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

private fun openLink(context: Context, link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    context.startActivity(intent)
}