package bg.zahov.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.util.SettingsChangeListener
import bg.zahov.app.ui.custom.RadioGroupSettingsView
import bg.zahov.app.ui.custom.SwitchSettingsView
import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.Units
import bg.zahov.app.hideBottomNav
import bg.zahov.app.data.local.Settings
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        requireActivity().hideBottomNav()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.slide_up)
        exitTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.fade_out)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        requireActivity().setToolBarTitle(R.string.settings_text)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_settings, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    R.id.reset -> {
                        settingsViewModel.resetSettings()
                        true
                    }

                    else -> false
                }
            }
        })

        binding.apply {
            signOutButton.setOnClickListener {
                settingsViewModel.logout()
                findNavController().navigate(R.id.settings_to_welcome)
            }

//                findNavController().navigate(R.id.settings_to_home)
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
                settingsViewModel.deleteAccount()
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
                    listOf(Units.BANANA.name, Units.METRIC.name),
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
                editProfile.setViewTitle("Edit")
                github.setViewTitle("Github")
                bugReport.setViewTitle("Bug report")
            }
        }
    }

    private fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}