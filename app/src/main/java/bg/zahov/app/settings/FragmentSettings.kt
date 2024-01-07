package bg.zahov.app.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.AuthViewModel
import bg.zahov.app.common.SettingsChangeListener
import bg.zahov.app.custom_views.RadioGroupSettingsView
import bg.zahov.app.custom_views.SwitchSettingsView
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.hideBottomNav
import bg.zahov.app.backend.Settings
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentSettingsBinding

class FragmentSettings : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels({ requireActivity() })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            signOutButton.setOnClickListener {
                authViewModel.signOut()
                findNavController().navigate(R.id.settings_to_welcome)
            }
            back.setOnClickListener {
                findNavController().navigate(R.id.settings_to_home)
            }
            resetSettings.setOnClickListener {
                settingsViewModel.resetSettings()
            }
            editProfile.setOnClickListener {
                findNavController().navigate(R.id.settings_to_edit_profile)
            }
            github.setOnClickListener {
                openLink("https://github.com/HauntedMilkshake/fitness_app")
            }
            bugReport.setOnClickListener {
                openLink("https://github.com/HauntedMilkshake/fitness_app/issues")
            }
            deleteAccount.setOnClickListener {
                authViewModel.deleteAccount()
                findNavController().navigate(R.id.settings_to_welcome)
            }

        }
        initDefaultSettingsViews()

    }

    private fun initRadioSettingsView(
        view: RadioGroupSettingsView,
        title: String,
        radioOptions: List<String>,
        settings: Settings,
    ) {
        view.initViewInformation(title, radioOptions, settings)
        view.settingsChangeListener = object : SettingsChangeListener {
            override fun onSettingChanged(title: String, newValue: Any) {
                settingsViewModel.writeNewSetting(title, newValue)
            }
        }
    }

    private fun initSwitchSettingsView(
        view: SwitchSettingsView,
        title: String,
        subtitle: String,
        settings: Settings,
    ) {
        view.initViewInformation(title, subtitle, settings)
        view.settingsChangeListener = object : SettingsChangeListener {
            override fun onSettingChanged(title: String, newValue: Any) {
                settingsViewModel.writeNewSetting(title, newValue)
            }
        }
    }

    private fun initDefaultSettingsViews() {
        binding.apply {
            settingsViewModel.settings.observe(viewLifecycleOwner) {
                initRadioSettingsView(
                    languageSettings,
                    "Language",
                    listOf(Language.English.name, Language.Bulgarian.name),
                    it
                )
                initRadioSettingsView(
                    unitSettings,
                    "Units",
                    listOf(Units.Banana.name, Units.Metric.name),
                    it
                )
                initRadioSettingsView(
                    themeSettings,
                    "Theme",
                    listOf(Theme.Light.name, Theme.Dark.name),
                    it
                )
                initRadioSettingsView(
                    restTimerSettings,
                    "Timer increment value",
                    listOf("30 s", "15 s", "5 s"),
                    it
                )
                initRadioSettingsView(
                    soundSettings,
                    "Sound",
                    listOf(Sound.SOUND_1.name, Sound.SOUND_2.name, Sound.SOUND_3.name),
                    it
                )
                initSwitchSettingsView(
                    soundEffectsSettings,
                    "Sound effects",
                    "Doesn't include rest timer alert",
                    it
                )
                initSwitchSettingsView(vibrateSettings, "Vibrate upon finish", "", it)
                initSwitchSettingsView(
                    samsungFitSettings,
                    "Use samsung watch during workout",
                    "",
                    it
                )
                initSwitchSettingsView(
                    showUpdateTemplateSettings,
                    "Show update template",
                    "Prompt when a workout is finished",
                    it
                )
                initSwitchSettingsView(
                    autoSyncSettings,
                    "Automatic between device sync",
                    "Turn this on if you want to use your account on another device",
                    it
                )
                editProfile.initViewInformation("Edit")
                github.initViewInformation("Github")
                bugReport.initViewInformation("Bug report")
            }
        }
    }

    private fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }
}