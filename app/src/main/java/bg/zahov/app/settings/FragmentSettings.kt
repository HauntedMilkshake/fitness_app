package bg.zahov.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.hideBottomNav
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentSettingsBinding

class FragmentSettings: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                settingsViewModel.logout()
                findNavController().navigate(R.id.settings_to_welcome)
            }
            back.setOnClickListener {
                findNavController().navigate(R.id.settings_to_home)
            }
            resetSettings.setOnClickListener {
                //TODO(reset settings)
            }
        }
        initDefaultSettingsViews()

    }

    private fun initDefaultSettingsViews() {
        binding.apply {
            settingsViewModel.settings.observe(viewLifecycleOwner) {
                //make sure the lists have the same names as the types defined
                languageSettings.initViewInformation("Language", listOf(Language.English.name, Language.Bulgarian.name), it, settingsViewModel)
                weightSettings.initViewInformation("Weight", listOf(Units.Banana.name, Units.Normal.name), it, settingsViewModel)
                distanceSettings.initViewInformation("Distance", listOf(Units.Banana.name, Units.Normal.name),it, settingsViewModel)
                soundEffectsSettings.initViewInformation("Sound effects", "Doesn't include rest timer alert", true)
                themeSettings.initViewInformation("Theme", listOf(Theme.Light.name, Theme.Dark.name), it, settingsViewModel)
                restTimerSettings.initViewInformation("Timer increment value", listOf("30s", "15s", "5s"), it, settingsViewModel)
                vibrateSettings.initViewInformation("Vibrate upon Finish", "", true)
                soundSettings.initViewInformation("Sound", listOf(Sound.SOUND_1.name, Sound.SOUND_2.name, Sound.SOUND_3.name),it, settingsViewModel)
                showUpdateTemplateSettings.initViewInformation("Show update template", "Prompt when a workout is finished", true)
                turnSyncOnSettings.initViewInformation("Sync with cloud", "Enable syncing to cloud", true)
                samsungFitSettings.initViewInformation("Use samsung watch during workout", "", false)

            }
        }

    }
}