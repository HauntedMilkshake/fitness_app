package bg.zahov.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.Switch
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.settings.SettingsViewModel
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class SwitchSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {
    init {
        inflate(context, R.layout.switch_settings_view, this)
    }
    fun initViewInformation(title: String, subTitle: String, settings: Settings, settingsVM: SettingsViewModel){
        val titleView = findViewById<MaterialTextView>(R.id.titleTextView)
        val subTitleView = findViewById<MaterialTextView>(R.id.subtitleTextView)
        val switchView = findViewById<Switch>(R.id.settings_switch)

        val state = when(title){
            "Sound effects" -> settings.soundEffects
            "Vibrate upon finish" -> settings.vibration
            "Use samsung watch during workout" -> settings.fit
            "Show update template" -> settings.updateTemplate
            else -> false
        }
        titleView.text = title
        subTitleView.text = subTitle
        switchView.isChecked = state

        switchView.setOnCheckedChangeListener { buttonView, isChecked ->
            handleSwitchStateChanged(title, isChecked, settingsVM)
        }
    }
    private fun handleSwitchStateChanged(title: String, isChecked: Boolean, settingsVM: SettingsViewModel){
        when(title){
            "Sound effects" -> settingsVM.writeNewSetting(title, isChecked)
            "Vibrate upon finish" -> settingsVM.writeNewSetting(title, isChecked)
            "Use samsung watch during workout" -> settingsVM.writeNewSetting(title, isChecked)
            "Show update template" -> settingsVM.writeNewSetting(title, isChecked)
        }
    }

}
