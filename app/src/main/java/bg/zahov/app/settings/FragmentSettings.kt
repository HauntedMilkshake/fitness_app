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
                settingsViewModel.resetSettings()
                settingsViewModel.refreshSettings()
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
        }
        initDefaultSettingsViews()

    }
    private fun initDefaultSettingsViews() {
        binding.apply {
                settingsViewModel.settings.observe(viewLifecycleOwner){
                    languageSettings.initViewInformation("Language", listOf(Language.English.name, Language.Bulgarian.name), it, settingsViewModel)
                    weightSettings.initViewInformation("Weight", listOf(Units.Banana.name, Units.Normal.name), it, settingsViewModel)
                    distanceSettings.initViewInformation("Distance", listOf(Units.Banana.name, Units.Normal.name),it, settingsViewModel)
                    soundEffectsSettings.initViewInformation("Sound effects", "Doesn't include rest timer alert", it, settingsViewModel)
                    themeSettings.initViewInformation("Theme", listOf(Theme.Light.name, Theme.Dark.name), it, settingsViewModel)
                    restTimerSettings.initViewInformation("Timer increment value", listOf("30 s", "15 s", "5 s"), it, settingsViewModel)
                    vibrateSettings.initViewInformation("Vibrate upon finish", "", it, settingsViewModel)
                    soundSettings.initViewInformation("Sound", listOf(Sound.SOUND_1.name, Sound.SOUND_2.name, Sound.SOUND_3.name),it, settingsViewModel)
                    showUpdateTemplateSettings.initViewInformation("Show update template", "Prompt when a workout is finished", it, settingsViewModel)
                    turnSyncOnSettings.initViewInformation("Sync with cloud", "Enable syncing to cloud", it, settingsViewModel)
                    samsungFitSettings.initViewInformation("Use samsung watch during workout", "", it, settingsViewModel)
                    editProfile.initViewInformation("Edit")
                    github.initViewInformation("Github")
                    bugReport.initViewInformation("Bug report")

                }
        }
    }
    private fun openLink(link: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }
}