package bg.zahov.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.Switch
import bg.zahov.app.common.SettingsChangeListener
import bg.zahov.app.realm_db.Settings
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class SwitchSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RelativeLayout(context, attrs, defStyle) {

    var settingsChangeListener: SettingsChangeListener? = null

    init {
        inflate(context, R.layout.switch_settings_view, this)
    }

    fun initViewInformation(title: String, subTitle: String, settings: Settings) {
        val titleView = findViewById<MaterialTextView>(R.id.titleTextView)
        val subTitleView = findViewById<MaterialTextView>(R.id.subtitleTextView)
        val switchView = findViewById<Switch>(R.id.settings_switch)

        val state = when (title) {
            "Sound effects" -> settings.soundEffects
            "Vibrate upon finish" -> settings.vibration
            "Use samsung watch during workout" -> settings.fit
            "Show update template" -> settings.updateTemplate
            else -> false
        }
        titleView.text = title
        subTitleView.text = subTitle
        switchView.isChecked = state

        switchView.setOnCheckedChangeListener { _, isChecked ->
            handleSwitchStateChanged(title, isChecked)
        }
    }

    private fun handleSwitchStateChanged(title: String, isChecked: Boolean) {
        settingsChangeListener?.onSettingChanged(title, isChecked)
    }
}